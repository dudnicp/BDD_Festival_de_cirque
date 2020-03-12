# PROJET BASES DE DONNES ENSIMAG-2019
# Organisation d'un festival de cirque
### Groupe 3 - IFG1


## Contenu des répertoires du projet

  * src : contient l'ensemble des sources java, ainsi qu'un dossier de scripts sql pour l'initialisation et la destruction des tables de la base de données
  * bin : contient l'ensemble des .class du projet (vide avant compilation)
  * jars : contient l'ensemble des librairies .jar nécessaires au projet (ojdbc6.jar)

## Utilisation de l'application

### Initialisation de la base de données

  * Se connecter au serveur oracle1 avec ses identifiants
  
  * Des scripts SQL sont proposés pour la création et l'initialisation des tables
  
    - Se positionner dans le répertoire src/script_sql et executer la commande
    ```
    @@init_tables.sql
    ```
    qui créera les tables nécessaires au fonctionnement l'application
    
    - L'application est prête à etre utilisée!
    
    
  * Le script précédent crée des tables vides, prêtes à etre remplies manuellement depuis l'application.
    Pour initialiser les tables avec des valeurs aléatoires :

    - Un script sql est proposé pour ajouter des artistes à la base de données :
    ```
    @@init_artists.sql
    ```
   
    - Un script java est proposé en complément du précédent script SQL pour initialiser les spécialités des artistes, des numéros et les avaluations des numéros (nécessite d'avoir lancé le script init_artists au préalable)
    
    - Depuis le répertoire source de l'application, lancer la commande
    ```
    make init
    ```
    
    - La base de données est initialisée !

### Lancement de l'application

  * Nécessite la création des tables au préalable
  * Depuis le répertoire source de l'application, lancer la commande
  ```
  make runApp
  ```

## Destruction des tables

  * Se connecter au serveur oracle1 avec ses identifiants et depuis le répertoire src/sql_scripts lancer la commande
  ```
  @@drop_tables.sql
  ```
