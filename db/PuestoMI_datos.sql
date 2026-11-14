-- ============================================================================
-- PuestoMI - Datos iniciales de productos (cargar DESPUES de PuestoMI.sql)
-- Origen: productos_refresqueria_para_bd_1.xlsx (201 productos)
-- * codigo_corto generado automaticamente (editable); stock_maximo = 2 x existencia inicial (editable)
-- * existencia inicial cargada como una entrada por proveedor con su lote (sin caducidad)
-- ============================================================================
USE PuestoMI;


INSERT INTO categoria (nombre) VALUES ('Bebidas'), ('Botanas'), ('Dulces');

INSERT INTO proveedor (nombre, empresa, activo) VALUES
  ('Arizona', 'Arizona', 1),
  ('Barcel', 'Barcel', 1),
  ('Bimbo', 'Bimbo', 1),
  ('Bonafont', 'Bonafont', 1),
  ('Coca-Cola', 'Coca-Cola', 1),
  ('De la Rosa', 'De la Rosa', 1),
  ('Herdez', 'Herdez', 1),
  ('Jarritos', 'Jarritos', 1),
  ('Jumex', 'Jumex', 1),
  ('Lala', 'Lala', 1),
  ('Lucas', 'Lucas', 1),
  ('Marinela', 'Marinela', 1),
  ('Mundet', 'Mundet', 1),
  ('Nestlé', 'Nestlé', 1),
  ('Pascual', 'Pascual', 1),
  ('PepsiCo', 'PepsiCo', 1),
  ('Ricolino', 'Ricolino', 1),
  ('Sabritas', 'Sabritas', 1),
  ('Santa Clara', 'Santa Clara', 1),
  ('Suerox', 'Suerox', 1),
  ('Tutsi', 'Tutsi', 1),
  ('Varios', 'Varios', 1),
  ('Vero', 'Vero', 1),
  ('Vita', 'Vita', 1);

INSERT INTO producto (codigo_corto, nombre, id_categoria, id_proveedor, costo, precio_venta, stock_minimo, stock_maximo, activo) VALUES
  ('cocacol355ltori', 'Coca-Cola 355 ml lata Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.85, 18.00, 6, 24, 1),
  ('cocacol355ori', 'Coca-Cola 355 ml Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.85, 18.00, 6, 24, 1),
  ('cocacol600ori', 'Coca-Cola 600 ml Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.50, 22.00, 6, 24, 1),
  ('cocacol1lori', 'Coca-Cola 1 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 25.38, 35.00, 6, 24, 1),
  ('cocacol135lori', 'Coca-Cola 1.35 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 28.00, 38.00, 6, 24, 1),
  ('cocacol2lori', 'Coca-Cola 2 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 34.62, 45.00, 6, 24, 1),
  ('cocacol25lori', 'Coca-Cola 2.5 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 41.15, 55.00, 6, 24, 1),
  ('cocacol3lori', 'Coca-Cola 3 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 39.23, 52.00, 6, 24, 1),
  ('cocacol600sinazu', 'Coca-Cola 600 ml Sin Azúcar', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.46, 18.00, 6, 24, 1),
  ('cocacol2lsinazu', 'Coca-Cola 2 L Sin Azúcar', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 34.62, 45.00, 6, 24, 1),
  ('cocacol600lig', 'Coca-Cola 600 ml Light', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 17.31, 24.00, 6, 24, 1),
  ('cocacol355ltsinazu', 'Coca-Cola 355 ml lata Sin Azúcar', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.85, 18.00, 6, 24, 1),
  ('spri355ltlimlim', 'Sprite 355 ml lata Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.85, 18.00, 6, 24, 1),
  ('spri600limlim', 'Sprite 600 ml Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.92, 22.00, 6, 24, 1),
  ('spri15llimlim', 'Sprite 1.5 L Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 22.31, 30.00, 6, 24, 1),
  ('spri2llimlim', 'Sprite 2 L Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 29.23, 38.00, 6, 24, 1),
  ('fant355ltnar', 'Fanta 355 ml lata Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.85, 18.00, 6, 24, 1),
  ('fant600nar', 'Fanta 600 ml Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.92, 22.00, 6, 24, 1),
  ('fant15lnar', 'Fanta 1.5 L Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 22.31, 30.00, 6, 24, 1),
  ('fant2lnar', 'Fanta 2 L Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 29.23, 38.00, 6, 24, 1),
  ('fres355tor', 'Fresca 355 ml Toronja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.85, 18.00, 6, 24, 1),
  ('fres600tor', 'Fresca 600 ml Toronja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.92, 22.00, 6, 24, 1),
  ('fres2ltor', 'Fresca 2 L Toronja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 29.23, 38.00, 6, 24, 1),
  ('sidrmun355man', 'Sidral Mundet 355 ml Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Mundet'), 13.85, 18.00, 6, 24, 1),
  ('sidrmun600man', 'Sidral Mundet 600 ml Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Mundet'), 16.92, 22.00, 6, 24, 1),
  ('sidrmun15lman', 'Sidral Mundet 1.5 L Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Mundet'), 22.31, 30.00, 6, 24, 1),
  ('sidrmun25lman', 'Sidral Mundet 2.5 L Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Mundet'), 36.00, 48.00, 6, 24, 1),
  ('delvalfiz600nar', 'Del Valle Fizz 600 ml Naranjada', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.92, 22.00, 6, 24, 1),
  ('delvalfiz600lim', 'Del Valle Fizz 600 ml Limonada', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.92, 22.00, 6, 24, 1),
  ('delval355nar', 'Del Valle 355 ml Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 13.85, 18.00, 6, 24, 1),
  ('delval600man', 'Del Valle 600 ml Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.92, 22.00, 6, 24, 1),
  ('delval600nar', 'Del Valle 600 ml Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 16.92, 22.00, 6, 24, 1),
  ('peps355ltori', 'Pepsi 355 ml lata Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 13.08, 17.00, 6, 24, 1),
  ('peps600ori', 'Pepsi 600 ml Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 13.85, 18.00, 6, 24, 1),
  ('peps15lori', 'Pepsi 1.5 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 22.31, 30.00, 6, 24, 1),
  ('peps2lori', 'Pepsi 2 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 26.92, 35.00, 6, 24, 1),
  ('peps25lori', 'Pepsi 2.5 L Original', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 32.31, 42.00, 6, 24, 1),
  ('peps600sinazu', 'Pepsi 600 ml Sin Azúcar', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 13.46, 18.00, 6, 24, 1),
  ('7up355ltlimlim', '7UP 355 ml lata Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 13.08, 17.00, 6, 24, 1),
  ('7up600limlim', '7UP 600 ml Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 16.92, 22.00, 6, 24, 1),
  ('7up15llimlim', '7UP 1.5 L Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 22.31, 30.00, 6, 24, 1),
  ('7up2llimlim', '7UP 2 L Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 29.23, 38.00, 6, 24, 1),
  ('miri355ltnar', 'Mirinda 355 ml lata Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 13.08, 17.00, 6, 24, 1),
  ('miri600nar', 'Mirinda 600 ml Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 16.92, 22.00, 6, 24, 1),
  ('miri15lnar', 'Mirinda 1.5 L Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 22.31, 30.00, 6, 24, 1),
  ('miri2lnar', 'Mirinda 2 L Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 29.23, 38.00, 6, 24, 1),
  ('jarr600man', 'Jarritos 600 ml Mandarina', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600pin', 'Jarritos 600 ml Piña', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600fre', 'Jarritos 600 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600tam', 'Jarritos 600 ml Tamarindo', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600tor', 'Jarritos 600 ml Toronja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600lim', 'Jarritos 600 ml Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600mang', 'Jarritos 600 ml Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600gua', 'Jarritos 600 ml Guayaba', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600san', 'Jarritos 600 ml Sandía', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('jarr600tutfru', 'Jarritos 600 ml Tutti Frutti', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 16.92, 22.00, 6, 24, 1),
  ('ariz600san', 'Arizona 600 ml Sandía', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 17.69, 24.00, 6, 24, 1),
  ('ariz600teneg', 'Arizona 600 ml Té Negro', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 17.69, 24.00, 6, 24, 1),
  ('ariz600man', 'Arizona 600 ml Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 17.69, 24.00, 6, 24, 1),
  ('ariz600tever', 'Arizona 600 ml Té Verde', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 17.69, 24.00, 6, 24, 1),
  ('ariz600kiwfre', 'Arizona 600 ml Kiwi-Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 17.69, 24.00, 6, 24, 1),
  ('ariz600mucman', 'Arizona 600 ml Mucho Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 17.69, 24.00, 6, 24, 1),
  ('ariz680teconmie', 'Arizona 680 ml Té con Miel', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 19.00, 25.00, 6, 24, 1),
  ('jume335man', 'Jumex 335 ml Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 13.85, 18.00, 6, 24, 1),
  ('jume335dur', 'Jumex 335 ml Durazno', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 13.85, 18.00, 6, 24, 1),
  ('jume335nar', 'Jumex 335 ml Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 13.85, 18.00, 6, 24, 1),
  ('jume335manz', 'Jumex 335 ml Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 13.85, 18.00, 6, 24, 1),
  ('jume335pin', 'Jumex 335 ml Piña', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 13.85, 18.00, 6, 24, 1),
  ('jume960man', 'Jumex 960 ml Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 23.08, 30.00, 6, 24, 1),
  ('jume960dur', 'Jumex 960 ml Durazno', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 23.08, 30.00, 6, 24, 1),
  ('jume960nar', 'Jumex 960 ml Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 23.08, 30.00, 6, 24, 1),
  ('jume960manz', 'Jumex 960 ml Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 23.08, 30.00, 6, 24, 1),
  ('jume960pin', 'Jumex 960 ml Piña', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 23.08, 30.00, 6, 24, 1),
  ('suer500uva', 'Suerox 500 ml Uva', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500coc', 'Suerox 500 ml Coco', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500hor', 'Suerox 500 ml Horchata', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500jam', 'Suerox 500 ml Jamaica', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500man', 'Suerox 500 ml Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500fre', 'Suerox 500 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500frekiw', 'Suerox 500 ml Fresa-Kiwi', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500dur', 'Suerox 500 ml Durazno', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('suer500pin', 'Suerox 500 ml Piña', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 18.46, 24.00, 6, 24, 1),
  ('gato600morazu', 'Gatorade 600 ml Mora Azul', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 20.77, 27.00, 6, 24, 1),
  ('gato600limlim', 'Gatorade 600 ml Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 20.77, 27.00, 6, 24, 1),
  ('gato600nar', 'Gatorade 600 ml Naranja', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 20.77, 27.00, 6, 24, 1),
  ('gato600pondefru', 'Gatorade 600 ml Ponche de Frutas', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 20.77, 27.00, 6, 24, 1),
  ('gato600cooblu', 'Gatorade 600 ml Cool Blue', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 20.77, 27.00, 6, 24, 1),
  ('gato1lmorazu', 'Gatorade 1 L Mora Azul', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 26.92, 35.00, 6, 24, 1),
  ('gato1llimlim', 'Gatorade 1 L Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 26.92, 35.00, 6, 24, 1),
  ('powe500mor', 'Powerade 500 ml Moras', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 19.23, 25.00, 6, 24, 1),
  ('powe500limlim', 'Powerade 500 ml Lima-Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 19.23, 25.00, 6, 24, 1),
  ('powe500fru', 'Powerade 500 ml Frutas', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 19.23, 25.00, 6, 24, 1),
  ('vita500fre', 'Vita 500 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vita'), 12.00, 17.00, 6, 24, 1),
  ('vita15lfre', 'Vita 1.5 L Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vita'), 22.00, 30.00, 6, 24, 1),
  ('agua500nat', 'Agua 500 ml Natural', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Bonafont'), 10.77, 15.00, 6, 24, 1),
  ('agua600nat', 'Agua 600 ml Natural', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Bonafont'), 11.54, 15.00, 6, 24, 1),
  ('agua1lnat', 'Agua 1 L Natural', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Bonafont'), 13.08, 17.00, 6, 24, 1),
  ('agua15lnat', 'Agua 1.5 L Natural', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Bonafont'), 15.38, 20.00, 6, 24, 1),
  ('agua2lnat', 'Agua 2 L Natural', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Bonafont'), 17.69, 24.00, 6, 24, 1),
  ('santcla180fre', 'Santa Clara 180 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Santa Clara'), 13.85, 18.00, 6, 24, 1),
  ('santcla180cho', 'Santa Clara 180 ml Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Santa Clara'), 13.85, 18.00, 6, 24, 1),
  ('santcla180vai', 'Santa Clara 180 ml Vainilla', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Santa Clara'), 13.85, 18.00, 6, 24, 1),
  ('santcla250cho', 'Santa Clara 250 ml Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Santa Clara'), 18.46, 24.00, 6, 24, 1),
  ('santcla250vai', 'Santa Clara 250 ml Vainilla', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Santa Clara'), 18.46, 24.00, 6, 24, 1),
  ('santcla250fre', 'Santa Clara 250 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Santa Clara'), 18.46, 24.00, 6, 24, 1),
  ('lala250cho', 'Lala 250 ml Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lala'), 13.85, 18.00, 6, 24, 1),
  ('lala250fre', 'Lala 250 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lala'), 13.85, 18.00, 6, 24, 1),
  ('lala250vai', 'Lala 250 ml Vainilla', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lala'), 13.85, 18.00, 6, 24, 1),
  ('yomi190cho', 'Yomi 190 ml Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lala'), 13.08, 17.00, 6, 24, 1),
  ('fuzetea600lim', 'Fuze Tea 600 ml Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 18.46, 24.00, 6, 24, 1),
  ('fuzetea600dur', 'Fuze Tea 600 ml Durazno', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 18.46, 24.00, 6, 24, 1),
  ('fuzetea600fruroj', 'Fuze Tea 600 ml Frutos Rojos', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 18.46, 24.00, 6, 24, 1),
  ('fuzetea15llim', 'Fuze Tea 1.5 L Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 24.62, 32.00, 6, 24, 1),
  ('fuzetea15ldur', 'Fuze Tea 1.5 L Durazno', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 24.62, 32.00, 6, 24, 1),
  ('nest600lim', 'Nestea 600 ml Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Nestlé'), 17.69, 24.00, 6, 24, 1),
  ('nest1llim', 'Nestea 1 L Limón', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Nestlé'), 22.00, 30.00, 6, 24, 1),
  ('boin250man', 'Boing 250 ml Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 10.00, 13.00, 6, 24, 1),
  ('boin250gua', 'Boing 250 ml Guayaba', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 10.00, 13.00, 6, 24, 1),
  ('boin250fre', 'Boing 250 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 10.00, 13.00, 6, 24, 1),
  ('boin250manz', 'Boing 250 ml Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 10.00, 13.00, 6, 24, 1),
  ('boin250uva', 'Boing 250 ml Uva', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 10.00, 13.00, 6, 24, 1),
  ('boin500man', 'Boing 500 ml Mango', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 15.38, 20.00, 6, 24, 1),
  ('boin500gua', 'Boing 500 ml Guayaba', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 15.38, 20.00, 6, 24, 1),
  ('boin500fre', 'Boing 500 ml Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 15.38, 20.00, 6, 24, 1),
  ('boin500manz', 'Boing 500 ml Manzana', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 15.38, 20.00, 6, 24, 1),
  ('boin500uva', 'Boing 500 ml Uva', (SELECT id_categoria FROM categoria WHERE nombre='Bebidas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 15.38, 20.00, 6, 24, 1),
  ('sabrbolchiori', 'Sabritas Bolsa chica Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 14.62, 20.00, 6, 30, 1),
  ('sabrbolchiado', 'Sabritas Bolsa chica Adobadas', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 14.62, 20.00, 6, 30, 1),
  ('sabrbolchilim', 'Sabritas Bolsa chica Limón', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 14.62, 20.00, 6, 30, 1),
  ('sabrbolchihab', 'Sabritas Bolsa chica Habanero', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 14.62, 20.00, 6, 30, 1),
  ('sabrbolmedori', 'Sabritas Bolsa mediana Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 20.00, 27.00, 6, 30, 1),
  ('sabrbolmedado', 'Sabritas Bolsa mediana Adobadas', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 20.00, 27.00, 6, 30, 1),
  ('sabrbolmedlim', 'Sabritas Bolsa mediana Limón', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 20.00, 27.00, 6, 30, 1),
  ('ruffbolchiori', 'Ruffles Bolsa chica Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('ruffbolchique', 'Ruffles Bolsa chica Queso', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('ruffbolchijal', 'Ruffles Bolsa chica Jalapeño', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('ruffbolmedori', 'Ruffles Bolsa mediana Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 20.00, 27.00, 6, 30, 1),
  ('doribolchinac', 'Doritos Bolsa chica Nacho', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('doribolchidin', 'Doritos Bolsa chica Dinamita', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('doribolchiflahot', 'Doritos Bolsa chica Flamin'' Hot', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('doribolmednac', 'Doritos Bolsa mediana Nacho', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 20.00, 27.00, 6, 30, 1),
  ('cheebolchique', 'Cheetos Bolsa chica Queso', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 27.00, 6, 30, 1),
  ('cheebolchitor', 'Cheetos Bolsa chica Torciditos', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 27.00, 6, 30, 1),
  ('cheebolchicol', 'Cheetos Bolsa chica Colmillos', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 27.00, 6, 30, 1),
  ('fritbolchisalylim', 'Fritos Bolsa chica Sal y Limón', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('rancbolchiori', 'Rancheritos Bolsa chica Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('tostbolchiori', 'Tostitos Bolsa chica Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('tostbolmedori', 'Tostitos Bolsa mediana Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 20.00, 27.00, 6, 30, 1),
  ('churbolchichiylim', 'Churrumais Bolsa chica Chile y Limón', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('chicsabbolori', 'Chicharrones Sabritas Bolsa Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('sabrbolori', 'Sabritones Bolsa Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('poffbolchique', 'Poffets Bolsa chica Queso', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('crujbolchique', 'Crujitos Bolsa chica Queso', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('pake265gbotsur', 'Paketaxo 265 g Botanas surtidas', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 47.69, 62.00, 6, 30, 1),
  ('pake265gmez', 'Paketaxo 265 g Mezcladito', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 47.69, 62.00, 6, 30, 1),
  ('pake215gflahot', 'Paketaxo 215 g Flamin'' Hot', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 42.31, 55.00, 6, 30, 1),
  ('tosttosbolelo', 'Tostitos Tostielote Bolsa Elote', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 20.00, 6, 30, 1),
  ('takibolchifue', 'Takis Bolsa chica Fuego', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 15.38, 20.00, 6, 30, 1),
  ('takibolchiori', 'Takis Bolsa chica Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 15.38, 20.00, 6, 30, 1),
  ('takibolchinit', 'Takis Bolsa chica Nitro', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 15.38, 20.00, 6, 30, 1),
  ('takibolmedfue', 'Takis Bolsa mediana Fuego', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 29.23, 38.00, 6, 30, 1),
  ('barcchibolfue', 'Barcel Chips Bolsa Fuego', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 15.38, 20.00, 6, 30, 1),
  ('barcchiboljal', 'Barcel Chips Bolsa Jalapeño', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 15.38, 20.00, 6, 30, 1),
  ('hotnutbolori', 'Hot Nuts Bolsa Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 15.38, 20.00, 6, 30, 1),
  ('hotnutbolhab', 'Hot Nuts Bolsa Habanero', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 15.38, 20.00, 6, 30, 1),
  ('cacajapbolori', 'Cacahuates japoneses Bolsa Original', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Varios'), 9.00, 18.00, 6, 30, 1),
  ('chursompieori', 'Churros Sombrilla Pieza Original', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Varios'), 18.00, 30.00, 8, 40, 1),
  ('churimppieori', 'Churros Imperio Pieza Original', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Varios'), 8.50, 25.00, 8, 40, 1),
  ('churdelsolbolori', 'Churros del Sol Bolsa Original', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Varios'), 15.00, 30.00, 8, 40, 1),
  ('duvapieavevai', 'Duvalín Pieza Avellana-Vainilla', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Ricolino'), 6.15, 8.00, 8, 40, 1),
  ('duvapiefrevai', 'Duvalín Pieza Fresa-Vainilla', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Ricolino'), 6.15, 8.00, 8, 40, 1),
  ('duvapieavefre', 'Duvalín Pieza Avellana-Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Ricolino'), 6.15, 8.00, 8, 40, 1),
  ('pulppietam', 'Pulparindo Pieza Tamarindo', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='De la Rosa'), 3.85, 5.00, 8, 40, 1),
  ('pulpboltam', 'Pulparindo Bolsa Tamarindo', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='De la Rosa'), 15.38, 20.00, 8, 40, 1),
  ('pulppieextpic', 'Pulparindo Pieza Extra Picante', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='De la Rosa'), 3.85, 5.00, 8, 40, 1),
  ('pelopelricpietam', 'Pelón Pelo Rico Pieza Tamarindo', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Herdez'), 7.69, 10.00, 8, 40, 1),
  ('lucamuepiecha', 'Lucas Muecas Pieza Chamoy', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lucas'), 7.69, 10.00, 8, 40, 1),
  ('lucamuepiesan', 'Lucas Muecas Pieza Sandía', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lucas'), 7.69, 10.00, 8, 40, 1),
  ('lucaguspiecha', 'Lucas Gusano Pieza Chamoy', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lucas'), 7.69, 10.00, 8, 40, 1),
  ('lucaguspiesan', 'Lucas Gusano Pieza Sandía', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lucas'), 7.69, 10.00, 8, 40, 1),
  ('skwibolsal', 'Skwinkles Bolsa Salsagheti', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lucas'), 13.85, 18.00, 8, 40, 1),
  ('skwibolrel', 'Skwinkles Bolsa Rellenos', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Lucas'), 13.85, 18.00, 8, 40, 1),
  ('rockpiepal', 'Rockaleta Pieza Paleta', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vero'), 9.23, 12.00, 8, 40, 1),
  ('tutspoppiefre', 'Tutsi Pop Pieza Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Tutsi'), 7.69, 10.00, 8, 40, 1),
  ('tutspoppiesur', 'Tutsi Pop Pieza Surtida', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Tutsi'), 7.69, 10.00, 8, 40, 1),
  ('veromanpie', 'Vero Mango Pieza', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vero'), 6.15, 8.00, 8, 40, 1),
  ('veroelopie', 'Vero Elote Pieza', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vero'), 6.15, 8.00, 8, 40, 1),
  ('veromanpiefru', 'Vero Manita Pieza Frutas', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vero'), 6.15, 8.00, 8, 40, 1),
  ('verotarpiechi', 'Vero Tarrito Pieza Chile', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vero'), 6.15, 8.00, 8, 40, 1),
  ('verorebpiesan', 'Vero Rebanaditas Pieza Sandía', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Vero'), 6.15, 8.00, 8, 40, 1),
  ('mazapiecac', 'Mazapán Pieza Cacahuate', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='De la Rosa'), 5.38, 8.00, 8, 40, 1),
  ('palepaypiecho', 'Paleta Payaso Pieza Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Ricolino'), 13.85, 18.00, 8, 40, 1),
  ('palepaypiechobla', 'Paleta Payaso Pieza Chocolate Blanco', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Ricolino'), 13.85, 18.00, 8, 40, 1),
  ('bubupiefre', 'Bubulubu Pieza Fresa', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Ricolino'), 11.54, 15.00, 8, 40, 1),
  ('ganspiefrecho', 'Gansito Pieza Fresa-Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Marinela'), 13.85, 18.00, 8, 40, 1),
  ('pingpiecho', 'Pingüinos Pieza Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Marinela'), 13.85, 18.00, 8, 40, 1),
  ('chocpiepin', 'Chocorroles Pieza Piña', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Marinela'), 15.38, 20.00, 8, 40, 1),
  ('nitopiecho', 'Nito Pieza Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Bimbo'), 12.31, 17.00, 8, 40, 1),
  ('mamupiecho', 'Mamut Pieza Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Marinela'), 9.23, 12.00, 8, 40, 1),
  ('kranpiecho', 'Kranky Pieza Chocolate', (SELECT id_categoria FROM categoria WHERE nombre='Dulces'), (SELECT id_proveedor FROM proveedor WHERE nombre='Nestlé'), 11.54, 15.00, 8, 40, 1),
  ('cheebolchiflahot', 'Cheetos Bolsa chica Flamin'' Hot', (SELECT id_categoria FROM categoria WHERE nombre='Botanas'), (SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 15.38, 27.00, 8, 40, 1);

-- Existencia inicial: una entrada por proveedor + un lote por producto
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Arizona'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ariz600san'), 12, 12, 17.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ariz600teneg'), 12, 12, 17.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ariz600man'), 12, 12, 17.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ariz600tever'), 12, 12, 17.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ariz600kiwfre'), 12, 12, 17.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ariz600mucman'), 12, 12, 17.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ariz680teconmie'), 12, 12, 19.00);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Barcel'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='takibolchifue'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='takibolchiori'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='takibolchinit'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='takibolmedfue'), 15, 15, 29.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='barcchibolfue'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='barcchiboljal'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='hotnutbolori'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='hotnutbolhab'), 15, 15, 15.38);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Bimbo'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='nitopiecho'), 20, 20, 12.31);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Bonafont'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='agua500nat'), 12, 12, 10.77),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='agua600nat'), 12, 12, 11.54),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='agua1lnat'), 12, 12, 13.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='agua15lnat'), 12, 12, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='agua2lnat'), 12, 12, 17.69);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Coca-Cola'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol355ltori'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol355ori'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol600ori'), 12, 12, 16.50),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol1lori'), 12, 12, 25.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol135lori'), 12, 12, 28.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol2lori'), 12, 12, 34.62),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol25lori'), 12, 12, 41.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol3lori'), 12, 12, 39.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol600sinazu'), 12, 12, 13.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol2lsinazu'), 12, 12, 34.62),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol600lig'), 12, 12, 17.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cocacol355ltsinazu'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='spri355ltlimlim'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='spri600limlim'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='spri15llimlim'), 12, 12, 22.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='spri2llimlim'), 12, 12, 29.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fant355ltnar'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fant600nar'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fant15lnar'), 12, 12, 22.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fant2lnar'), 12, 12, 29.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fres355tor'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fres600tor'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fres2ltor'), 12, 12, 29.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='delvalfiz600nar'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='delvalfiz600lim'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='delval355nar'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='delval600man'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='delval600nar'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='powe500mor'), 12, 12, 19.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='powe500limlim'), 12, 12, 19.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='powe500fru'), 12, 12, 19.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fuzetea600lim'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fuzetea600dur'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fuzetea600fruroj'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fuzetea15llim'), 12, 12, 24.62),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fuzetea15ldur'), 12, 12, 24.62);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='De la Rosa'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pulppietam'), 20, 20, 3.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pulpboltam'), 20, 20, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pulppieextpic'), 20, 20, 3.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='mazapiecac'), 20, 20, 5.38);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Herdez'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pelopelricpietam'), 20, 20, 7.69);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Jarritos'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600man'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600pin'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600fre'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600tam'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600tor'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600lim'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600mang'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600gua'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600san'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jarr600tutfru'), 12, 12, 16.92);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Jumex'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume335man'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume335dur'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume335nar'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume335manz'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume335pin'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume960man'), 12, 12, 23.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume960dur'), 12, 12, 23.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume960nar'), 12, 12, 23.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume960manz'), 12, 12, 23.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='jume960pin'), 12, 12, 23.08);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Lala'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='lala250cho'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='lala250fre'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='lala250vai'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='yomi190cho'), 12, 12, 13.08);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Lucas'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='lucamuepiecha'), 20, 20, 7.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='lucamuepiesan'), 20, 20, 7.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='lucaguspiecha'), 20, 20, 7.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='lucaguspiesan'), 20, 20, 7.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='skwibolsal'), 20, 20, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='skwibolrel'), 20, 20, 13.85);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Marinela'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ganspiefrecho'), 20, 20, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pingpiecho'), 20, 20, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='chocpiepin'), 20, 20, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='mamupiecho'), 20, 20, 9.23);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Mundet'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sidrmun355man'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sidrmun600man'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sidrmun15lman'), 12, 12, 22.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sidrmun25lman'), 12, 12, 36.00);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Nestlé'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='nest600lim'), 12, 12, 17.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='nest1llim'), 12, 12, 22.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='kranpiecho'), 20, 20, 11.54);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Pascual'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin250man'), 12, 12, 10.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin250gua'), 12, 12, 10.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin250fre'), 12, 12, 10.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin250manz'), 12, 12, 10.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin250uva'), 12, 12, 10.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin500man'), 12, 12, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin500gua'), 12, 12, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin500fre'), 12, 12, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin500manz'), 12, 12, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='boin500uva'), 12, 12, 15.38);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='PepsiCo'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='peps355ltori'), 12, 12, 13.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='peps600ori'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='peps15lori'), 12, 12, 22.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='peps2lori'), 12, 12, 26.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='peps25lori'), 12, 12, 32.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='peps600sinazu'), 12, 12, 13.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='7up355ltlimlim'), 12, 12, 13.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='7up600limlim'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='7up15llimlim'), 12, 12, 22.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='7up2llimlim'), 12, 12, 29.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='miri355ltnar'), 12, 12, 13.08),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='miri600nar'), 12, 12, 16.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='miri15lnar'), 12, 12, 22.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='miri2lnar'), 12, 12, 29.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='gato600morazu'), 12, 12, 20.77),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='gato600limlim'), 12, 12, 20.77),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='gato600nar'), 12, 12, 20.77),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='gato600pondefru'), 12, 12, 20.77),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='gato600cooblu'), 12, 12, 20.77),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='gato1lmorazu'), 12, 12, 26.92),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='gato1llimlim'), 12, 12, 26.92);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Ricolino'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='duvapieavevai'), 20, 20, 6.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='duvapiefrevai'), 20, 20, 6.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='duvapieavefre'), 20, 20, 6.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='palepaypiecho'), 20, 20, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='palepaypiechobla'), 20, 20, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='bubupiefre'), 20, 20, 11.54);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Sabritas'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolchiori'), 15, 15, 14.62),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolchiado'), 15, 15, 14.62),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolchilim'), 15, 15, 14.62),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolchihab'), 15, 15, 14.62),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolmedori'), 15, 15, 20.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolmedado'), 15, 15, 20.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolmedlim'), 15, 15, 20.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ruffbolchiori'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ruffbolchique'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ruffbolchijal'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='ruffbolmedori'), 15, 15, 20.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='doribolchinac'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='doribolchidin'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='doribolchiflahot'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='doribolmednac'), 15, 15, 20.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cheebolchique'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cheebolchitor'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cheebolchicol'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='fritbolchisalylim'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='rancbolchiori'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='tostbolchiori'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='tostbolmedori'), 15, 15, 20.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='churbolchichiylim'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='chicsabbolori'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='sabrbolori'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='poffbolchique'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='crujbolchique'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pake265gbotsur'), 15, 15, 47.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pake265gmez'), 15, 15, 47.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='pake215gflahot'), 15, 15, 42.31),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='tosttosbolelo'), 15, 15, 15.38),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cheebolchiflahot'), 20, 20, 15.38);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Santa Clara'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='santcla180fre'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='santcla180cho'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='santcla180vai'), 12, 12, 13.85),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='santcla250cho'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='santcla250vai'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='santcla250fre'), 12, 12, 18.46);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Suerox'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500uva'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500coc'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500hor'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500jam'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500man'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500fre'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500frekiw'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500dur'), 12, 12, 18.46),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='suer500pin'), 12, 12, 18.46);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Tutsi'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='tutspoppiefre'), 20, 20, 7.69),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='tutspoppiesur'), 20, 20, 7.69);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Varios'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='cacajapbolori'), 15, 15, 9.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='chursompieori'), 20, 20, 18.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='churimppieori'), 20, 20, 8.50),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='churdelsolbolori'), 20, 20, 15.00);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Vero'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='rockpiepal'), 20, 20, 9.23),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='veromanpie'), 20, 20, 6.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='veroelopie'), 20, 20, 6.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='veromanpiefru'), 20, 20, 6.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='verotarpiechi'), 20, 20, 6.15),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='verorebpiesan'), 20, 20, 6.15);
INSERT INTO entrada_inventario (id_proveedor, id_usuario, observaciones) VALUES ((SELECT id_proveedor FROM proveedor WHERE nombre='Vita'), 1, 'Existencia inicial');
SET @e := LAST_INSERT_ID();
INSERT INTO lote (id_entrada, id_producto, cantidad_recibida, cantidad_disponible, costo_unitario) VALUES
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='vita500fre'), 12, 12, 12.00),
  (@e, (SELECT id_producto FROM producto WHERE codigo_corto='vita15lfre'), 12, 12, 22.00);
