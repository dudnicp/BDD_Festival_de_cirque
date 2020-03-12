import java.sql.*;
import java.util.ArrayList;


public class Evaluation {

	public static final int maxEvaluations = 15;
	public static final int nExpertsInJury = 5;
	public static final int nExpertsSpe = 3;

	public static final int maxMark = 10;


	private int idExpert;
	private int idAct;
	private int mark;
	private String comment;

	public Evaluation(int idExpert, int idAct) {
		this.idExpert = idExpert;
		this.idAct = idAct;
		mark = 0;
		comment = "lorem ipsum";
	}

	public void addToDataBase() throws SQLException {
		String query = String.format("INSERT INTO EVALUATIONS VALUES (%s, %s, %s, '%s')", idExpert, idAct, mark, comment);
		Exec.dataBase.executeUpdate(query);
	}

	public static void createJury() throws SQLException {
		int idEvaluatedAct = -1;
		boolean isActOk = false;
		while (!isActOk) {
			System.out.println("ID Numéro à évaluer :");
			idEvaluatedAct = Integer.parseInt(Exec.scanner.nextLine());
			String query = "SELECT IDNUM FROM NUMEROS WHERE IDNUM = " + idEvaluatedAct;
			ResultSet res = Exec.dataBase.executeQuery(query);
			if (res.next()) {
	    		isActOk = true;
			}
			else {
				System.out.println("Aucun numéro portant cet identifiant dans la base de données, réessayez.");
			}

			res.close();
		}

		String query = "SELECT THEME, CIRQUE FROM NUMEROS WHERE IDNUM = " + idEvaluatedAct;
		ResultSet res = Exec.dataBase.executeQuery(query);
		res.next();
		String theme = res.getString("THEME");
		String circus = res.getString("CIRQUE");

		String allArtists = "SELECT IDARTISTE FROM ARTISTES";
		String participants = "SELECT DISTINCT IDARTISTE FROM PARTICIPEA";
		String allExperts = String.format("SELECT IDARTISTE as id FROM (%s) MINUS (%s)", allArtists, participants);
		String availableExperts = String.format("SELECT IDARTISTE as expert FROM (%s) ", allExperts);
		availableExperts += String.format("JOIN ARTISTES ON id = ARTISTES.IDARTISTE WHERE CIRQUE != '%s'", circus);
		String availableExpertsNotTheme = String.format("SELECT DISTINCT IDARTISTE FROM (%s) JOIN ESTSPE ON expert = ESTSPE.IDARTISTE WHERE NOMSPE != '%s'", availableExperts, theme);
		String availableExpertsTheme = String.format("SELECT DISTINCT IDARTISTE FROM (%s) JOIN ESTSPE ON expert = ESTSPE.IDARTISTE WHERE NOMSPE = '%s'", availableExperts, theme);
		availableExpertsNotTheme = String.format("SELECT IDARTISTE FROM (%s) MINUS (%s)", availableExpertsNotTheme, availableExpertsTheme);

		ResultSet resTheme = Exec.dataBase.executeQuery(availableExpertsTheme);
		ResultSet resNotTheme = Exec.dataBase.executeQuery(availableExpertsNotTheme);


		int count = 0;
		int[] jury = new int[5];

		while (count < nExpertsSpe && resTheme.next()) {
			int tryExpertID = resTheme.getInt("IDARTISTE");
			if (getNumberEvaluations(tryExpertID, Exec.dataBase) < maxEvaluations) {
				Evaluation eval = new Evaluation(tryExpertID, idEvaluatedAct);
				eval.addToDataBase();
				jury[count] = tryExpertID;
				count++;
			}
		}
		if (count < nExpertsSpe) {
			System.out.println("Pas assez d'experts de spécialité différente disponibles !");
		}

		while (count < nExpertsInJury && resNotTheme.next()) {
			int tryExpertID = resNotTheme.getInt("IDARTISTE");
			if (getNumberEvaluations(tryExpertID, Exec.dataBase) < maxEvaluations) {
				Evaluation eval = new Evaluation(tryExpertID, idEvaluatedAct);
				eval.addToDataBase();
				jury[count] = tryExpertID;
				count++;
			}
		}
		if (count < nExpertsInJury) {
			System.out.println("Pas assez d'experts spécialisés disponibles !");
		}

		String showExperts = "Les membres du jury sont les artistes numéro : ";
		for (int idExpert : jury) {
			showExperts += idExpert + " ";
		}
		System.out.println(showExperts);

		Exec.dataBase.closeRes(res);
		Exec.dataBase.closeRes(resNotTheme);
		Exec.dataBase.closeRes(resTheme);
	}

	static public void updateEvaluation() throws SQLException {
		int idAct = -1;
		boolean isActOk = false;
		while (!isActOk) {
			System.out.println("ID Numéro à évaluer :");
			idAct = Integer.parseInt(Exec.scanner.nextLine());

			String query = "SELECT IDEXPERT FROM EVALUATIONS WHERE IDNUM = " + idAct;

			ArrayList<Integer> juryMembers = new ArrayList<Integer>();

			ResultSet res = Exec.dataBase.executeQuery(query);
			while (res.next()) {
				juryMembers.add(res.getInt("IDEXPERT"));
			}


			if (!juryMembers.isEmpty()) {
				for (Integer idExpert : juryMembers) {
					System.out.println("Evaluation de l'expert " + idExpert + " :");

					int mark = -1;
					boolean isMarkOK = false;
					while (!isMarkOK) {
						System.out.println("Note :");
						mark = Integer.parseInt(Exec.scanner.nextLine());
						if (mark >= 0 && mark <= 10) {
							isMarkOK = true;
						}
						else {
							System.out.println("Note invalide, doit etre entre 0 et 10, réessayez.");
						}
					}

					boolean isCommOk = false;
					String comment = "";
					while (!isCommOk) {
						System.out.println("Commentaire :");
						comment = Exec.scanner.nextLine();

						if (!comment.equals("")) {
							isCommOk = true;
						}
						else {
							System.out.println("Commentaire invalide, ne peut pas etre vide, réessayez.");
						}
					}

					String query1 = String.format("UPDATE EVALUATIONS SET NOTE = %s WHERE IDEXPERT = %s AND IDNUM = %s", mark, idExpert, idAct);
					String query2 = String.format("UPDATE EVALUATIONS SET COMMENTAIRE = '%s' WHERE IDEXPERT = %s AND IDNUM = %s", comment, idExpert, idAct);

					Exec.dataBase.executeUpdate(query1);
					Exec.dataBase.executeUpdate(query2);
				}
				isActOk = true;
			}
			else {
				System.out.println("Ce numero n'a pas de jury assigné, réessayez.");
			}

			Exec.dataBase.closeRes(res);
		}
	}

	public static int getNumberEvaluations(int idExpert, DataBase dataBase) throws SQLException {
		String query = "SELECT COUNT(*) as total FROM EVALUATIONS WHERE IDEXPERT = " + idExpert;
		ResultSet res = dataBase.executeQuery(query);
		res.next();
		int n = res.getInt("total");
		dataBase.closeRes(res);
		return n;
	}
}
