package persistencia;

import java.sql.SQLException;
import java.util.List;

public interface I_CRUD<T> {
    void crear(T t) throws SQLException;
    T leerPorId(int id) throws SQLException;
    void actualizar(T t) throws SQLException;
    void eliminar(int id) throws SQLException;
    List<T> leerTodos() throws SQLException;
}