# Test technique Velco - Paul PETIT

### Projet

Le projet est un projet Spring Boot en Java 8 et qui utilise maven comme gestionnaire de build et docker pour
l'exécution.

### Utilisation

- Cloner le projet git sur sa machine
- Ouvrir un terminal à la racine du projet
- Lancer le projet avec docker
    - Faire un build de l'image ``docker build -t test-technique-paul-petit .``
    - Lancer un conteneur à partir de l'image ``docker run -d -p 8080:8080 test-technique-paul-petit``
- Faire une requête au
  service ``curl --location --request POST 'localhost:8080/convert' --form 'file=@"src/test/resources/testfiles/Ref_FinalTest.txt"'``

### Détails techniques

Le contrôleur **ConvertController** expose l'endpoint **/convert**.

Cet endpoint attend une requête POST avec des form-data.

| Clé     | Description                                                                                      | Obligatoire ? |
|---------|--------------------------------------------------------------------------------------------------|---------------|
| file    | Le fichier a envoyer                                                                             | oui           |
| sortKey | La clé sur laquelle trier (price ou size)<br/>Si elle n'est pas présente, aucun tri ne sera fait | non           |

Ensuite le service **RequestValidationService** va valider la requête. Si elle n'est pas correcte un retour HTTP 400
(Bad Request) sera retourné avec la liste des erreurs dans le body JSON.

Par exemple dans le cas d'une requête sans le **file** et avec la **sortKey** incorrecte, le retour sera le suivant :

```
{
    "errors": [
        {
            "key": "file",
            "error": "The file is mandatory"
        },
        {
            "key": "sortKey",
            "error": "The sortkey must be one of [price, size]"
        }
    ]
}
```

Ensuite le service **ConvertService** va convertir le fichier d'entrée vers le format de sortie.

Si tout se passe correctement, le service fait un retour HTTP 200 (OK) avec dans le body un JSON avec le format
suivant :

```
{
    "inputFile": "Ref_FinalTest.txt",
    "references": [
        {
            "numReference": "1460100040",
            "size": 27,
            "price": 45.12,
            "type": "R"
        },
        {
            "numReference": "1460900848",
            "size": 145,
            "price": 12.0,
            "type": "G"
        },
        {
            "numReference": "1462100044",
            "size": 19,
            "price": 5.56,
            "type": "G"
        },
        {
            "numReference": "1462100403",
            "size": 97,
            "price": 105.23,
            "type": "B"
        }
    ],
    "errors": [
        {
            "line": 5,
            "message": "Incorrect value for color, must be R, G or B",
            "value": "1462100403;A;100.1;9"
        }
    ]
}
```

Ici les valeurs sont dans l'ordre des lignes du fichier d'entrée, comme aucun tri n'a été demandé.

Dans le cas où l'on demande un tri sur **price** par exemple, le retour sera le suivant :

```
{
    "inputFile": "Ref_FinalTest.txt",
    "references": [
        {
            "numReference": "1462100044",
            "size": 19,
            "price": 5.56,
            "type": "G"
        },
        {
            "numReference": "1460900848",
            "size": 145,
            "price": 12.0,
            "type": "G"
        },
        {
            "numReference": "1460100040",
            "size": 27,
            "price": 45.12,
            "type": "R"
        },
        {
            "numReference": "1462100403",
            "size": 97,
            "price": 105.23,
            "type": "B"
        }
    ],
    "errors": [
        {
            "line": 5,
            "message": "Incorrect value for color, must be R, G or B",
            "value": "1462100403;A;100.1;9"
        }
    ]
}
```

Le même résultat qu'avant mais avec les références triées par **price** ascendant

### Points d'amélioration

- Le tri sur les clés **price** et **sortKey** est forcément ascendant. On pourrait imaginer une clé **order**
  optionnelle (tri ascendant par défaut) qui prends les valeurs **ASC** ou **DESC** pour choisir le sens voulu.
- Les messages d'erreurs sont en dur dans le code. On pourrait imaginer un fichier **.properties** contenant tous ces
  messages et les rejoindre avec une clé connue dans le code.
- Améliorer la validation du fichier envoyer. Pour l'instant, on vérifie seulement si le type mime du fichier est
  **text/plain**, ce qui bloquerai un PDF par exemple, mais le contenu du fichier texte envoyé n'est pas vraiment
  validé.
