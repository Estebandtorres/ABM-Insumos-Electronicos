import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsumosDAO {
    private final Conexion conexion = new Conexion();
//registro
    public boolean registrarInsumo(Insumos insumo) throws SQLException {
        String sql = "INSERT INTO insumos (codigo, nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = conexion.establecerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            cargarParametros(ps, insumo);
            return ps.executeUpdate() > 0;
        }
    }
//modificacion
    public boolean modificarInsumo(Insumos insumo) throws SQLException {
        String sql = "UPDATE insumos SET codigo = ?, nombre = ?, descripcion = ?, precio = ?, stock = ? WHERE id = ?";

        try (Connection con = conexion.establecerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            cargarParametros(ps, insumo);
            ps.setInt(6, insumo.getId());
            return ps.executeUpdate() > 0;
        }
    }
//eliminacion
    public boolean eliminarInsumo(int id) throws SQLException {
        String sql = "DELETE FROM insumos WHERE id = ?";

        try (Connection con = conexion.establecerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Insumos> listarInsumos() throws SQLException {
        String sql = "SELECT id, codigo, nombre, descripcion, precio, stock FROM insumos ORDER BY id DESC";
        List<Insumos> insumos = new ArrayList<>();

        try (Connection con = conexion.establecerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                insumos.add(mapearInsumo(rs));
            }
        }

        return insumos;
    }
    //busqueda de insumos

    public List<Insumos> buscarInsumos(String texto) throws SQLException {
        String sql = "SELECT id, codigo, nombre, descripcion, precio, stock FROM insumos "
                + "WHERE codigo LIKE ? OR nombre LIKE ? OR descripcion LIKE ? ORDER BY id DESC";
        List<Insumos> insumos = new ArrayList<>();
        String busqueda = "%" + texto + "%";

        try (Connection con = conexion.establecerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    insumos.add(mapearInsumo(rs));
                }
            }
        }

        return insumos;
    }

    private void cargarParametros(PreparedStatement ps, Insumos insumo) throws SQLException {
        ps.setString(1, insumo.getCodigo());
        ps.setString(2, insumo.getNombre());
        ps.setString(3, insumo.getDescripcion());
        ps.setDouble(4, insumo.getPrecio());
        ps.setInt(5, insumo.getStock());
    }
    //mapeado de insumos

    private Insumos mapearInsumo(ResultSet rs) throws SQLException {
        return new Insumos(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("stock"));
    }
}
