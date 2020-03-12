import java.sql.*;
import java.util.ArrayList;

public class Act {
	private int id;
	private String title;
	private String theme;
	private String summary;
	private int duration;
	private String circus;
	private int idMainArtist;

	public Act() throws SQLException {

	    String query = "SELECT MAX(IDNUM) AS idMax FROM NUMEROS";
		ResultSet res = Exec.dataBase.executeQuery(query);
		if (res.next()) {
			id = res.getInt("idMax") + 1;
		}
		else {
			id = 1;
		}

		System.out.println("L'ID du nouveau numéro est " + id);
		Exec.waitInput();


		System.out.println("Informations demandées :");

		System.out.println("Titre :");
	    title = Exec.scanner.nextLine();

	    boolean isThemeOK = false;
		while (!isThemeOK) {
			System.out.println("Thème :");
			theme = (Exec.scanner.nextLine()).toUpperCase();
			query = "SELECT NOMSPE FROM SPECIALITES WHERE NOMSPE = '" + theme + "'";
			res = Exec.dataBase.executeQuery(query);
			if (res.next()) {
				isThemeOK = true;
			}
			else {
				System.out.println("Ce thème ne fait pas partie des thèmes du festival, réessayez.");
			}
		}


	    System.out.println("Résumé :");
	    summary = Exec.scanner.nextLine();

	    boolean isDurationOK = false;
	    while (!isDurationOK) {
	    	System.out.println("Durée :");
	    	duration = Integer.parseInt(Exec.scanner.nextLine());
	    	if (duration > 30 || duration < 10 ) {
	    		System.out.println("La durée du numéro doit être comprise entre 10 et 30 minutes, réessayez.");
	    	} else {
	    		isDurationOK = true;
	    	}
	    }

	    idMainArtist = -1;
	    boolean isArtistOK = false;
	    while (!isArtistOK) {
	    	System.out.println("ID Artiste principal :");
	    	idMainArtist = Integer.parseInt(Exec.scanner.nextLine());
		    String queryIDArtist = "SELECT IDARTISTE FROM ARTISTES WHERE IDARTISTE = " + idMainArtist;
		    res = Exec.dataBase.executeQuery(queryIDArtist);
	    	if (res.next()) {
				isArtistOK = true;
			}
	    	else {
		    	System.out.println("Aucun artiste portant ce numéro dans la base de données, réessayez.");
		    }
	    }

	    query = "SELECT CIRQUE FROM ARTISTES WHERE IDARTISTE = " + idMainArtist;
	    res = Exec.dataBase.executeQuery(query);
	    res.next();
	    circus = res.getString("CIRQUE");

	    query = "SELECT MAX(IDNUM) AS idMax FROM NUMEROS";
		res = Exec.dataBase.executeQuery(query);
		if (res.next()) {
			id = res.getInt("idMax") + 1;
		}
		else {
			id = 1;
		}

		System.out.println("--- L'ID du numéro est " + id + " ---");

		Exec.dataBase.closeRes(res);
	}

	public void addToDataBase() throws SQLException {
		String query = String.format("INSERT INTO NUMEROS VALUES (%s, '%s', '%s', '%s', %s, '%s', %s)",
								id, title, theme.toUpperCase(), summary, duration, circus, idMainArtist);
		System.out.println("Ajout du numéro à la base de données...");
		Exec.dataBase.executeUpdate(query);

		query = String.format("INSERT INTO PARTICIPEA VALUES(%s, %s)", idMainArtist, id);
		System.out.println("Ajout automatique de l'artiste principal aux artistes participants au numéro...");
		Exec.dataBase.executeUpdate(query);
	}

	public void addArtist() throws SQLException {
		int idArtist = -1;
		boolean isArtistOk = false;
		while (!isArtistOk) {
			System.out.println("ID Artiste :");
			idArtist = Integer.parseInt(Exec.scanner.nextLine());

			String query = "SELECT CIRQUE FROM ARTISTES WHERE IDARTISTE = " + idArtist;
			ResultSet res = Exec.dataBase.executeQuery(query);
		    if (res.next()) {
		    	String artistCircus = res.getString("CIRQUE");
		    	if (artistCircus.equals(circus)) {
		    		isArtistOk = true;
				} else {
					System.out.println("Cet artiste n'est pas du même cirque que l'artiste principal du numero, réessayez");
				}
			}
		    else {
		    	System.out.println("Aucun artiste portant cet identifiant dans la base de données, réessayez.");
		    }

		    query = "SELECT IDARTISTE FROM PARTICIPEA WHERE IDNUM = " + id;
		    res = Exec.dataBase.executeQuery(query);

		    ArrayList<Integer> participantsIds = new ArrayList<Integer>();;
		    while (res.next()) {
		    	participantsIds.add(res.getInt("IDARTISTE"));
			}
		    if (participantsIds.contains(idArtist)) {
				isArtistOk = false;
				System.out.println("Cet artiste participe déjà au numéro, réessayez.");
			}

		    Exec.dataBase.closeRes(res);
		}

		String query = String.format("INSERT INTO PARTICIPEA VALUES(%s, %s)", idArtist, id);
		Exec.dataBase.executeUpdate(query);
	}

	public static int getDuration(int idAct) throws SQLException {
		String query = "SELECT DUREE FROM NUMEROS WHERE IDNUM = " + idAct;
		ResultSet res = Exec.dataBase.executeQuery(query);
		int duration = -1;
		if (res.next()) {
			duration =  res.getInt(1);

		}
		else {
			System.out.println("Aucun numéro portant cet identifiant dans la vase de données.");
		}

		Exec.dataBase.closeRes(res);

		return duration;
	}

	public static void showInfo() {
		try {
			System.out.println("ID Numéro :");
			int idAct = Integer.parseInt(Exec.scanner.nextLine());

			Exec.clearConsole();

			String query = "SELECT * FROM NUMEROS WHERE IDNUM = " + idAct;
			ResultSet res = Exec.dataBase.executeQuery(query);
			System.out.println("Informations sur le numéro " + idAct);
			Exec.dataBase.showResults(res, false);
			System.out.println();

			query = "SELECT NOTE, COMMENTAIRE FROM EVALUATIONS WHERE IDNUM = " + idAct;
			res = Exec.dataBase.executeQuery(query);
			System.out.println("Notes obtenues lors de l'évaluation du jury :");
			Exec.dataBase.showResults(res, false);

			query = "SELECT AVG(NOTE) FROM EVALUATIONS WHERE IDNUM = " +idAct;
			res = Exec.dataBase.executeQuery(query);
			System.out.println("Moyenne :");
			Exec.dataBase.showResults(res, true);
			System.out.println();

			query = "SELECT IDARTISTE FROM PARTICIPEA WHERE IDNUM = " + idAct;
			res = Exec.dataBase.executeQuery(query);
			System.out.println("Artistes participant à ce numéro :");
			Exec.dataBase.showResults(res, false);
			System.out.println();

			Exec.dataBase.closeRes(res);

		} catch (Exception e) {
			Exec.printError(e);
		}
	}
}
