pipeline {
    agent any // Se ejecuta en el contenedor principal de Jenkins

    stages {
        stage('Descargar Código') {
            steps {
                // Descarga el código de la rama actual de GitHub
                checkout scm
                echo 'Código descargado de GitHub correctamente.'
            }
        }

        stage('Instalar Dependencias') {
            steps {
                echo 'Instalando dependencias del proyecto...'

                // Comando por defecto para proyectos Node.js (Cypress, Playwright, WebDriverIO)
                sh 'npm install'

                // Si usas Python, comenta la línea de arriba y usa esta:
                // sh 'pip install -r requirements.txt'

                // Si usas Java/Maven, usa esta:
                // sh 'mvn clean install -DskipTests'
            }
        }

        stage('Ejecutar Pruebas') {
            steps {
                echo 'Ejecutando la suite de pruebas automatizadas...'

                // Comando por defecto para correr tests en Node.js
                sh 'npm test'

                // Si usas Python:
                // sh 'pytest'

                // Si usas Java/Maven:
                // sh 'mvn test'
            }
        }
    }

    // Esta sección siempre se ejecuta al final, fallen o pasen las pruebas
    post {
        always {
            echo 'Finalizó la ejecución.'
            // Cuando configures Allure, descomentarás la siguiente línea:
            // allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]

            echo 'Limpiando espacio de trabajo para no saturar el disco...'
            cleanWs()
        }
        success {
            echo '✅ Todas las pruebas pasaron exitosamente. El código es estable.'
        }
        failure {
            echo '❌ Algunas pruebas fallaron o hubo un error de compilación. Revisa los logs.'
        }
    }
}
