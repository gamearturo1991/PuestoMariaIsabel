package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Lote;
import puesto.maria.entity.Merma;
import puesto.maria.entity.Producto;
import puesto.maria.persistence.dao.MermaDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reglas de negocio de las mermas (PBI-MER): registrar con motivo, anular (rescatar piezas) y consultar por
 * motivo y periodo. Los errores de validación se lanzan como IllegalArgumentException con un mensaje listo
 * para mostrar.
 */
public class DelegateMerma {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private MermaDAO dao() {
        return ServiceLocator.getInstanceMermaDAO();
    }

    /** Motivos que se pueden elegir (lista fija del sistema). */
    public List<String> listarMotivos() {
        return CatalogoMotivos.motivos();
    }

    /**
     * Pérdida estimada de mermar N piezas de un producto: se toman de las entradas más antiguas, cada pieza
     * a su costo. Devuelve cero si los datos aún no alcanzan para calcularla.
     */
    public BigDecimal estimarPerdida(Integer idProducto, Integer cantidad) {
        if (idProducto == null || cantidad == null || cantidad < 1) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int restante = cantidad;
        for (Lote l : dao().lotesDisponibles(idProducto)) {
            if (restante == 0) {
                break;
            }
            int tomar = Math.min(restante, l.getCantidadDisponible());
            total = total.add(l.getCostoUnitario().multiply(BigDecimal.valueOf(tomar)));
            restante -= tomar;
        }
        return total;
    }

    /**
     * Registra una merma: descuenta de las entradas más antiguas las piezas indicadas (solo esas piezas, nunca
     * un lote completo) y guarda motivo, usuario y la pérdida a costo.
     */
    public ResultadoMerma registrarMerma(Integer idProducto, Integer cantidad, String motivo,
                                         String observaciones, Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("No hay un usuario con sesión iniciada.");
        }
        Producto producto = idProducto == null ? null
                : ServiceLocator.getInstanceProductoDAO().find(idProducto).orElse(null);
        if (producto == null) {
            throw new IllegalArgumentException("Selecciona un producto.");
        }
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad de piezas debe ser mayor a 0.");
        }
        validarMotivo(motivo);
        int existencia = dao().existenciaTotal(idProducto);
        if (cantidad > existencia) {
            throw new IllegalArgumentException("Solo hay " + existencia + " pieza(s) de "
                    + producto.getNombre() + " en existencia.");
        }

        String notas = (observaciones == null || observaciones.isBlank()) ? null : observaciones.trim();
        List<Merma> filas = dao().registrarMerma(idProducto, cantidad, motivo, notas, idUsuario);

        BigDecimal perdida = BigDecimal.ZERO;
        for (Merma m : filas) {
            perdida = perdida.add(m.getCostoUnitario().multiply(BigDecimal.valueOf(m.getCantidad())));
        }
        return new ResultadoMerma(producto, cantidad, perdida, dao().existenciaTotal(idProducto));
    }

    /**
     * Anula una merma de forma total o parcial: las piezas rescatadas regresan al lote del que salieron.
     * La descripción del error es obligatoria y la merma original se conserva en el historial.
     */
    public void anularMerma(RegistroMerma registro, Integer cantidad, String descripcion, Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("No hay un usuario con sesión iniciada.");
        }
        if (registro == null) {
            throw new IllegalArgumentException("Selecciona la merma que quieres anular.");
        }
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("Indica cuántas piezas se van a rescatar (mínimo 1).");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Describe cuál fue el error o por qué se mermó por accidente.");
        }
        dao().anularMerma(registro.getIds(), cantidad, descripcion.trim(), idUsuario);
    }

    /**
     * Mermas del periodo (de la fecha inicial a la final, ambas incluidas) y, opcionalmente, de un motivo.
     * Las más recientes primero.
     */
    public List<RegistroMerma> consultarMermas(String motivoFiltro, LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Indica la fecha inicial y la fecha final del periodo.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final.");
        }
        if (motivoFiltro != null && !motivoFiltro.isBlank()) {
            validarMotivo(motivoFiltro);
        } else {
            motivoFiltro = null;
        }

        ZoneId zona = ZoneId.systemDefault();
        Instant inicio = desde.atStartOfDay(zona).toInstant();
        Instant fin = hasta.plusDays(1).atStartOfDay(zona).toInstant();

        // Se agrupan las filas de una misma merma: misma fecha, usuario, producto y motivo.
        Map<String, List<Merma>> grupos = new LinkedHashMap<>();
        for (Merma m : dao().consultar(inicio, fin)) {
            String motivo = m.getMotivo() == null ? "" : m.getMotivo();
            if (motivoFiltro != null && !motivoFiltro.equalsIgnoreCase(motivo)) {
                continue;
            }
            String clave = m.getFechaHora() + "|" + m.getIdUsuario().getId() + "|"
                    + m.getIdLote().getIdProducto().getId() + "|" + motivo;
            grupos.computeIfAbsent(clave, k -> new ArrayList<>()).add(m);
        }

        // Las anulaciones son filas con piezas en negativo que apuntan a la merma original.
        List<Merma> anulaciones = dao().listarAnulaciones();
        Map<Integer, Long> rescatadas = MermaDAO.rescatadasPorMerma(anulaciones);
        Map<Integer, List<Merma>> anulacionesPorMerma = new LinkedHashMap<>();
        for (Merma a : anulaciones) {
            if (MermaDAO.esAnulacion(a)) {
                anulacionesPorMerma.computeIfAbsent(MermaDAO.idMermaAnulada(a), k -> new ArrayList<>()).add(a);
            }
        }

        List<RegistroMerma> registros = new ArrayList<>();
        for (List<Merma> filas : grupos.values()) {
            registros.add(armarRegistro(filas, rescatadas, anulacionesPorMerma, zona));
        }
        return registros;
    }

    /** Piezas netas y pérdida neta por motivo, para los totales del periodo consultado. */
    public List<ResumenMotivo> totalesPorMotivo(List<RegistroMerma> registros) {
        Map<String, ResumenMotivo> resumen = new LinkedHashMap<>();
        for (RegistroMerma r : registros) {
            resumen.computeIfAbsent(r.getMotivo(), ResumenMotivo::new).sumar(r.getPendientes(), r.getPerdidaNeta());
        }
        return new ArrayList<>(resumen.values());
    }

    private RegistroMerma armarRegistro(List<Merma> filas, Map<Integer, Long> rescatadas,
                                        Map<Integer, List<Merma>> anulacionesPorMerma, ZoneId zona) {
        Merma primera = filas.get(0);
        List<Integer> ids = new ArrayList<>();
        List<AnulacionRegistro> anulaciones = new ArrayList<>();
        int piezas = 0;
        long totalRescatadas = 0;
        BigDecimal bruta = BigDecimal.ZERO;
        BigDecimal neta = BigDecimal.ZERO;
        for (Merma m : filas) {
            ids.add(m.getId());
            long resc = rescatadas.getOrDefault(m.getId(), 0L);
            piezas += m.getCantidad();
            totalRescatadas += resc;
            bruta = bruta.add(m.getCostoUnitario().multiply(BigDecimal.valueOf(m.getCantidad())));
            neta = neta.add(m.getCostoUnitario().multiply(BigDecimal.valueOf(m.getCantidad() - resc)));
            for (Merma a : anulacionesPorMerma.getOrDefault(m.getId(), new ArrayList<>())) {
                anulaciones.add(new AnulacionRegistro(
                        a.getFechaHora().atZone(zona).format(FORMATO_FECHA), -a.getCantidad(),
                        MermaDAO.descripcionAnulacion(a),
                        nombreUsuario(a.getIdUsuario().getNombreCompleto(), a.getIdUsuario().getNombreUsuario())));
            }
        }
        return new RegistroMerma(
                primera.getFechaHora().atZone(zona).format(FORMATO_FECHA),
                primera.getIdLote().getIdProducto(),
                primera.getMotivo() == null ? "" : primera.getMotivo(),
                primera.getObservaciones(),
                nombreUsuario(primera.getIdUsuario().getNombreCompleto(), primera.getIdUsuario().getNombreUsuario()),
                ids, piezas, totalRescatadas, bruta, neta, anulaciones);
    }

    private static String nombreUsuario(String completo, String usuario) {
        return completo != null && !completo.isBlank() ? completo : usuario;
    }

    /** El motivo debe ser uno de la lista fija del sistema. */
    private void validarMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Selecciona el motivo de la merma.");
        }
        if (!CatalogoMotivos.esValido(motivo)) {
            throw new IllegalArgumentException("El motivo seleccionado no es válido.");
        }
    }
}
