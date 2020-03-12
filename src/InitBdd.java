import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class InitBdd {

	public static final int totalArtists = 100;
	public static final int maxParticipants = 70;
	public static final int maxSpe = 3;
	public static final int totalActs = 100;
	public static final int maxEvaluations = 15;

	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);

			System.out.println("Initialisation de la base de données");

			System.out.print("USER : ");
			String user = scanner.nextLine();
			System.out.println("PASSWORD : ");
			String passwd = scanner.nextLine();

			DataBase dataBase = new DataBase(user, passwd);


			System.out.println("Initialisation des valeurs de la base de données");
			System.out.println("Cela peut prendre quelques instants...");


			Random gen = new Random();

			String[] themesTab = {"MAGIE", "ACROBATIES", "EQUILIBRE", "JONGLAGE", "DRESSAGE", "CLOWN", "MARIONETTES", "DANSE"};

			/* INIT THEMES */

			System.out.println("Initialisation Themes du Festival");

			for (String theme : themesTab) {
				String query = "INSERT INTO SPECIALITES VALUES('" + theme + "')";
				dataBase.executeUpdate(query);
			}

			System.out.println("OK");


			/* INIT SPECIALITIES */

			System.out.println("Initialisation des spécialités des artistes");

			for (int idArtist = 1; idArtist <= totalArtists; idArtist++) {
				int nSpe = gen.nextInt(maxSpe) + 1;

				ArrayList<String> existingSpes = new ArrayList<String>();

				for (int j = 0; j < nSpe; j++) {
					String spe = themesTab[gen.nextInt(themesTab.length)];
					while (existingSpes.contains(spe)) {
						spe = themesTab[gen.nextInt(themesTab.length)];
					}
					existingSpes.add(spe);
				}

				for (String spe : existingSpes) {
					String query = String.format("INSERT INTO ESTSPE VALUES(%s, '%s')", idArtist, spe);
					dataBase.executeUpdate(query);
				}

				if (idArtist%10 == 0) {
					System.out.println(100 * ((double) idArtist)/totalArtists + "%");
				}
			}

			dataBase.commit();
			System.out.println("OK");

			/* INIT ACTS */

			System.out.println("Initialisation des numéros du festival");

			for (int idAct = 1; idAct <= totalActs; idAct++) {
				int idArtist = gen.nextInt(maxParticipants) + 1;

				String queryCircus = "SELECT CIRQUE FROM ARTISTES WHERE IDARTISTE = " + idArtist;
				String querySpe = "SELECT NOMSPE FROM ESTSPE WHERE IDARTISTE = " + idArtist;

				ResultSet resCircus = dataBase.executeQuery(queryCircus);
				ResultSet resSpe = dataBase.executeQuery(querySpe);

				ArrayList<String> artistSpes = new ArrayList<String>();
				while (resSpe.next()) {
					artistSpes.add(resSpe.getString(1));
				}

				resCircus.next();
				String circus = resCircus.getString(1);
				String theme = artistSpes.get(gen.nextInt(artistSpes.size()));
				int duration = gen.nextInt(5) * 5 + 10;

				String query = String.format("INSERT INTO NUMEROS VALUES (%s, '%s', '%s', '%s', %s, '%s', %s)",
						idAct, "Untitled", theme.toUpperCase(), "lorem ipsum", duration, circus, idArtist);
				dataBase.executeUpdate(query);

				query = String.format("INSERT INTO PARTICIPEA VALUES(%s, %s)", idArtist, idAct);
				dataBase.executeUpdate(query);

				dataBase.closeRes(resSpe);
				dataBase.closeRes(resCircus);

				if (idAct%10 == 0) {
					System.out.println(100 * ((double) idAct)/totalActs + "%");
				}
			}

			dataBase.commit();

			System.out.println("OK");


			/* INIT EVALUATIONS */

			System.out.println("Initialisation des évaluations des numéros");

			for (int idAct = 1; idAct <= totalActs; idAct++) {
				String query = "SELECT THEME, CIRQUE FROM NUMEROS WHERE IDNUM = " + idAct;
				ResultSet res = dataBase.executeQuery(query);
				res.next();
				String theme = res.getString("THEME");
				String circus = res.getString("CIRQUE");

				String allArtists = "SELECT IDARTISTE FROM ARTISTES";
				String participants = "SELECT DISTINCT IDARTISTE FROM PARTICIPEA";
				String allExperts = String.format("SELECT IDARTISTE as id FROM (%s) MINUS (%s)", allArtists, participants);
				String availableExperts = String.format("SELECT IDARTISTE as expert FROM (%s) ", allExperts);
				availableExperts += String.format("JOIN ARTISTES ON id = ARTISTES.IDARTISTE WHERE CIRQUE != '%s'", circus);

				String availableExpertsNotSpe = String.format("SELECT DISTINCT IDARTISTE FROM (%s) JOIN ESTSPE ON expert = ESTSPE.IDARTISTE WHERE NOMSPE != '%s'", availableExperts, theme);
				String availableExpertsSpe = String.format("SELECT DISTINCT IDARTISTE FROM (%s) JOIN ESTSPE ON expert = ESTSPE.IDARTISTE WHERE NOMSPE = '%s'", availableExperts, theme);
				availableExpertsNotSpe = String.format("SELECT IDARTISTE FROM (%s) MINUS (%s)", availableExpertsNotSpe, availableExpertsSpe);

				ResultSet resNotSpe = dataBase.executeQuery(availableExpertsNotSpe);
				ResultSet resSpe = dataBase.executeQuery(availableExpertsSpe);

				int count = 0;
				while (count < Evaluation.nExpertsSpe && resSpe.next()) {
					int idExpert = resSpe.getInt("IDARTISTE");

					if (Evaluation.getNumberEvaluations(idExpert, dataBase) < Evaluation.maxEvaluations) {
						int mark = gen.nextInt(Evaluation.maxMark);
						query = String.format("INSERT INTO EVALUATIONS VALUES(%s, %s, %s, 'lorem ipsum')", idExpert, idAct, mark);
						dataBase.executeUpdate(query);
						count++;
					}
				}

				while (count < Evaluation.nExpertsInJury && resNotSpe.next()) {
					int idExpert = resNotSpe.getInt("IDARTISTE");

					if (Evaluation.getNumberEvaluations(idExpert, dataBase) < Evaluation.maxEvaluations) {
						int mark = gen.nextInt(Evaluation.maxMark);
						query = String.format("INSERT INTO EVALUATIONS VALUES(%s, %s, %s, 'lorem ipsum')", idExpert, idAct, mark);
						dataBase.executeUpdate(query);
						count++;
					}
				}


				dataBase.closeRes(res);
				dataBase.closeRes(resNotSpe);
				dataBase.closeRes(resSpe);

				if (idAct%5 == 0) {
					System.out.println(100 * ((double) idAct)/totalArtists + "%");
				}
			}

			dataBase.commit();

			System.out.println("OK");

			System.out.println("Initialisation terminée ! Vous pouvez ajouter des Spectacles manuellement");

			dataBase.deconnect();

			scanner.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
