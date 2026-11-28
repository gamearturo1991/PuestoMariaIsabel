package Puesto.maria.isabel.delegate;

import puesto.maria.entity.CorteCaja;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Cortes de caja de un periodo con sus sumas, para la contabilidad. */
public class ResumenCortes implements Serializable {

    /** Un corte final con lo que se contó ese día (si quedó en la bitácora). */
    public static class FilaCorte implements Serializable {
        private final CorteCaja corte;
        private final BigDecimal efectivoEsperado;
        private final BigDecimal efectivoContado;
        private final BigDecimal diferencia;

        public FilaCorte(CorteCaja corte, BigDecimal efectivoEsperado, BigDecimal efectivoContado, BigDecimal diferencia) {
            this.corte = corte;
            this.efectivoEsperado = efectivoEsperado;
            this.efectivoContado = efectivoContado;
            this.diferencia = diferencia;
        }

        public CorteCaja getCorte() {
            return corte;
        }

        public BigDecimal getEfectivoEsperado() {
            return efectivoEsperado;
        }

        public BigDecimal getEfectivoContado() {
            return efectivoContado;
        }

        public BigDecimal getDiferencia() {
            return diferencia;
        }

        public boolean isConArqueo() {
            return diferencia != null;
        }

        public String getEstado() {
            if (diferencia == null) {
                return "Sin arqueo";
            }
            if (diferencia.signum() > 0) {
                return "Faltante";
            }
            return diferencia.signum() < 0 ? "Sobrante" : "Cuadrada";
        }
    }

    /** Un arqueo (conteo de caja) hecho durante el periodo. */
    public static class Arqueo implements Serializable {
        private final String fecha;
        private final String usuario;
        private final BigDecimal efectivoEsperado;
        private final BigDecimal efectivoContado;
        private final BigDecimal diferencia;

        public Arqueo(String fecha, String usuario, BigDecimal efectivoEsperado, BigDecimal efectivoContado,
                      BigDecimal diferencia) {
            this.fecha = fecha;
            this.usuario = usuario;
            this.efectivoEsperado = efectivoEsperado;
            this.efectivoContado = efectivoContado;
            this.diferencia = diferencia;
        }

        public String getFecha() {
            return fecha;
        }

        public String getUsuario() {
            return usuario;
        }

        public BigDecimal getEfectivoEsperado() {
            return efectivoEsperado;
        }

        public BigDecimal getEfectivoContado() {
            return efectivoContado;
        }

        public BigDecimal getDiferencia() {
            return diferencia;
        }

        public String getEstado() {
            if (diferencia == null) {
                return "";
            }
            if (diferencia.signum() > 0) {
                return "Faltante";
            }
            return diferencia.signum() < 0 ? "Sobrante" : "Cuadrada";
        }
    }

    private final PeriodoCortes tipo;
    private final LocalDate inicio;
    private final LocalDate fin;
    private final String titulo;
    private final List<FilaCorte> filas;
    private final List<Arqueo> arqueos;
    private final int diasSinCorte;

    private BigDecimal totalEfectivo = BigDecimal.ZERO;
    private BigDecimal totalTransferencia = BigDecimal.ZERO;
    private BigDecimal totalTarjeta = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalComision = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;
    private BigDecimal totalFaltantes = BigDecimal.ZERO;
    private BigDecimal totalSobrantes = BigDecimal.ZERO;

    public ResumenCortes(PeriodoCortes tipo, LocalDate inicio, LocalDate fin, String titulo, List<FilaCorte> filas,
                         List<Arqueo> arqueos, int diasSinCorte) {
        this.tipo = tipo;
        this.inicio = inicio;
        this.fin = fin;
        this.titulo = titulo;
        this.filas = filas == null ? new ArrayList<>() : filas;
        this.arqueos = arqueos == null ? new ArrayList<>() : arqueos;
        this.diasSinCorte = diasSinCorte;
        for (FilaCorte f : this.filas) {
            CorteCaja c = f.getCorte();
            totalEfectivo = totalEfectivo.add(valor(c.getTotalEfectivo()));
            totalTransferencia = totalTransferencia.add(valor(c.getTotalTransferencia()));
            totalTarjeta = totalTarjeta.add(valor(c.getTotalTarjeta()));
            totalDevoluciones = totalDevoluciones.add(valor(c.getTotalDevoluciones()));
            totalComision = totalComision.add(valor(c.getTotalComision()));
            totalNeto = totalNeto.add(valor(c.getTotalNeto()));
            if (f.getDiferencia() != null) {
                if (f.getDiferencia().signum() > 0) {
                    totalFaltantes = totalFaltantes.add(f.getDiferencia());
                } else if (f.getDiferencia().signum() < 0) {
                    totalSobrantes = totalSobrantes.add(f.getDiferencia().abs());
                }
            }
        }
    }

    private static BigDecimal valor(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public PeriodoCortes getTipo() {
        return tipo;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<FilaCorte> getFilas() {
        return filas;
    }

    public List<Arqueo> getArqueos() {
        return arqueos;
    }

    public int getDiasSinCorte() {
        return diasSinCorte;
    }

    public BigDecimal getTotalEfectivo() {
        return totalEfectivo;
    }

    public BigDecimal getTotalTransferencia() {
        return totalTransferencia;
    }

    public BigDecimal getTotalTarjeta() {
        return totalTarjeta;
    }

    public BigDecimal getTotalDevoluciones() {
        return totalDevoluciones;
    }

    public BigDecimal getTotalComision() {
        return totalComision;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public BigDecimal getTotalFaltantes() {
        return totalFaltantes;
    }

    public BigDecimal getTotalSobrantes() {
        return totalSobrantes;
    }
}
