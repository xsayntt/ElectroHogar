USE electrohogar;

DROP PROCEDURE IF EXISTS sp_listar_empleados_reportes;

DELIMITER //
CREATE PROCEDURE sp_listar_empleados_reportes(IN p_id_usuario INT)
BEGIN
    CALL sp_comprobar_permiso(p_id_usuario, 'FrmReportesVentas', 'R');

    SELECT e.ID_EMPLEADO,
           CONCAT(e.NOMBRES, ' ', e.APELLIDOS) AS NOMBRE_COMPLETO
      FROM EMPLEADO e
     ORDER BY e.NOMBRES, e.APELLIDOS;
END//
DELIMITER ;
