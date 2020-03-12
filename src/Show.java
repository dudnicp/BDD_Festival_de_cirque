import java.sql.*;
import java.util.Random;

public class Show {
	private int id;
	private String theme;
	private int day;
	private int hour;
	private int price;
	private int idHost;

	public Show() throws SQLException{

		String query = "SELECT MAX(IDSPECTACLE) AS idMax FROM SPECTACLES";
		ResultSet res = Exec.dataBase.executeQuery(query);
		if (res.next()) {
			id = res.getInt("idMax") + 1;
		}
		else {
			id = 1;
		}

		System.out.println("L'ID du nouveau spectacle est " + id);
		Exec.waitInput();


		System.out.println("Informations demandées :");

		boolean isThemeOK = false;
		while (!isThemeOK) {
			System.out.println("Thème :");
			theme = (Exec.scanner.nextLine()).toUpperCase();
			query = "SELECT NOMSPE FROM SPECIALITES WHERE NOMSPE = '" + theme + "'";
			res = Exec.dataBase.executeQuery(query);
			if (res.next()) {
				isThemeOK = true;
			}

			if (!isThemeOK) {
				System.out.println("Ce thème ne fait pas partie des thèmes du festival, réessayez.");
			}
		}

		boolean isDateOk = false;
		while (!isDateOk) {
			System.out.println("Jour :");
			day = Integer.parseInt(Exec.scanner.nextLine());
			System.out.println("Heure :");
			hour = Integer.parseInt(Exec.scanner.nextLine());
			if ((hour == 9 || hour == 14) && (day >= 1 && day <= 4)) {
				query = String.format("SELECT * FROM SPECTACLES WHERE JOUR = %s AND HEURE = %s", day, hour);
				res = Exec.dataBase.executeQuery(query);
				if (!res.next()) {
					isDateOk = true;
				}
				else {
					System.out.println("Il y a déjà un spectacle le même jour à la même heure, réessayez.");
				}
			} else {
				System.out.println("Date invalide : il faut 1 <= jour <= 4 et heure = 9 ou heure = 14, réessayez.");
			}
		}

		boolean isPriceOk = false;
		while (!isPriceOk) {
			System.out.println("Prix :");
			price = Integer.parseInt(Exec.scanner.nextLine());
			if (price >= 0) {
				isPriceOk = true;
			} else {
				System.out.println("Prix invalide : il faut prix >= 0, réessayez.");
			}
		}

		idHost = 1;

		Exec.dataBase.closeRes(res);
	}

	public void linkActsToShows() throws SQLException {
		System.out.println("Association de numéros à ce spectacle...");

		String allActs = String.format("SELECT IDNUM FROM NUMEROS WHERE THEME = '%s'", theme);
		String actsInAShow = String.format("SELECT NUMEROS.IDNUM FROM NUMEROS JOIN NUMAPPARTIENTA ON NUMEROS.IDNUM = NUMAPPARTIENTA.IDNUM WHERE NUMEROS.THEME = '%s'", theme);
		String actsavailable = String.format("SELECT IDNUM AS ACTSavailable FROM (%s) MINUS (%s)", allActs, actsInAShow);
		String query = "SELECT IDNUM, AVG(NOTE)"
				+ " FROM ((" + actsavailable + ") JOIN EVALUATIONS ON ACTSavailable = EVALUATIONS.IDNUM)"
				+ " GROUP BY IDNUM ORDER BY AVG(NOTE) DESC";

		ResultSet res = Exec.dataBase.executeQuery(query);


		int totalDuration = 0;
		while (totalDuration < 180 && res.next()) {
			int idAct = res.getInt("IDNUM");
			totalDuration += Act.getDuration(idAct);

			query = String.format("INSERT INTO NUMAPPARTIENTA VALUES(%s, %s)", id, idAct);
			Exec.dataBase.executeUpdate(query);
		}

		Exec.dataBase.closeRes(res);
	}

	public void linkHostToShow() throws SQLException {
		String allArtists = "SELECT IDARTISTE FROM ARTISTES";
		String participants = "SELECT IDARTISTE FROM "
				+ "PARTICIPEA JOIN NUMAPPARTIENTA ON PARTICIPEA.IDNUM = NUMAPPARTIENTA.IDNUM "
				+ "WHERE NUMAPPARTIENTA.IDSPECTACLE = " + id;
		String availableHostsQuery  = String.format("SELECT IDARTISTE FROM (%s) MINUS (%s)", allArtists, participants);
		String countAllQuery = "SELECT COUNT(*) as total FROM (" + availableHostsQuery + ")";

		ResultSet availableHostsRes = Exec.dataBase.executeQuery(availableHostsQuery);
		ResultSet totalRes = Exec.dataBase.executeQuery(countAllQuery);
		totalRes.next();
		int total = totalRes.getInt("total");

		Random gen = new Random();
		int hostIdIndex = gen.nextInt(total + 1);
		for (int i = 0; i < hostIdIndex; i++) {
			availableHostsRes.next();
		}

		idHost = availableHostsRes.getInt("IDARTISTE");

		String query1 = String.format("UPDATE SPECTACLES SET IDPRESENTATEUR = %s WHERE IDSPECTACLE = %s", idHost, id);
		System.out.println("Association d'un présentateur pour ce spectacle...");
		Exec.dataBase.executeUpdate(query1);

		Exec.dataBase.closeRes(totalRes);
		Exec.dataBase.closeRes(availableHostsRes);
	}

	public void addToDataBase() throws SQLException {
		String query = String.format("INSERT INTO SPECTACLES VALUES(%s, '%s', %s, %s, %s, %s)", id, theme, day, hour, price, idHost);
		System.out.println("Ajout du spectacle à la base de données...");
		Exec.dataBase.executeUpdate(query);
	}

	public static void showInfo() {
		try {
			System.out.println("ID Spectacle :");
			int idShow = Integer.parseInt(Exec.scanner.nextLine());

			Exec.clearConsole();

			String query = "SELECT * FROM SPECTACLES WHERE IDSPECTACLE = " + idShow;
			ResultSet res = Exec.dataBase.executeQuery(query);
			System.out.println("Informations sur le spectacle " + idShow);
			Exec.dataBase.showResults(res, false);
			System.out.println();

			query = "SELECT IDNUM FROM NUMAPPARTIENTA WHERE IDSPECTACLE = " + idShow;
			res = Exec.dataBase.executeQuery(query);
			System.out.println("Numéros composant ce spectacle :");
			Exec.dataBase.showResults(res, true);
			System.out.println();

			Exec.dataBase.closeRes(res);

		} catch (Exception e) {
			Exec.printError(e);
		}
	}
}
