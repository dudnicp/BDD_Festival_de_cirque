import java.sql.*;

public class DataBase{
	private String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
	private String USER;;
	private String PASSWD;

	private Connection connexion;

	public DataBase(String user, String passwd) throws SQLException{
		System.out.println("Chargement du driver Oracle...");
		DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
		System.out.println("Chargement effectué !");
		
		USER = user;
		PASSWD = passwd;

		System.out.println("Connexion à la base de données");
		connexion = DriverManager.getConnection(CONN_URL, USER, PASSWD);
		connexion.setAutoCommit(false);
		connexion.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		System.out.println("Connecté !");
	}

	public void deconnect() throws SQLException {
		System.out.println("Déconnexion de la base de données");
		connexion.close();
		System.out.println("Déconnecté !");
	}
	
	public ResultSet executeQuery(String query) throws SQLException {
		PreparedStatement statement = connexion.prepareStatement(query);
		ResultSet result = statement.executeQuery();
		return result;
	}
	
	public void executeUpdate(String query) throws SQLException {
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.executeUpdate();
		statement.close();
	}
	
	public void closeRes(ResultSet res) throws SQLException {
		Statement stmt = res.getStatement();
		res.close();
		stmt.close();
	}
	
	public void commit() throws SQLException {
		connexion.commit();					
	}
	
	public void rollback() {
		try {
			connexion.rollback();
		} 
		catch (SQLException e) {
			Exec.printError(e);
		}
	}
	
	public void showResults(ResultSet res, boolean hideColumnName) throws SQLException {
		String table = "";
		ResultSetMetaData resMeta = res.getMetaData();
		int columnCount = resMeta.getColumnCount();
		
		if (!hideColumnName) {
			for (int i = 1; i <= columnCount; i++) {
				table += resMeta.getColumnName(i).toUpperCase() + " \t ";
			}
			table += "\n";
		}
		
		while (res.next()) {
			for (int i = 1; i <= columnCount; i++) {
				String tmp = res.getString(i);
				if (tmp.matches("[0-9]{4}-.*")) {
					tmp = tmp.substring(0,10);
				}
				table += tmp + " \t ";
			}
			table += "\n";
		}
		System.out.print(table);
	}
	
	public void showTable(String tableName) throws SQLException {
		String query = "SELECT * FROM " + tableName.toUpperCase();
		ResultSet res = executeQuery(query);
		System.out.println("*** TABLE " + tableName + " ***");
		showResults(res, false);
	}
}

