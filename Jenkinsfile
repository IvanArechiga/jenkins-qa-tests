pipeline {
    agent any // Se ejecuta en el contenedor principal de Jenkins

    // INYECCIÓN DE HERRAMIENTAS: Aquí Jenkins carga Java y Maven
    // Los nombres deben coincidir EXACTAMENTE con los que pusiste en el Paso 2.1
    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    stages {
        stage('Descargar Código') {
            steps {
                // Descarga el código de la rama actual de GitHub
                checkout scm
                echo 'Código descargado de GitHub correctamente.'
            }
        }

        stage('Compilar Proyecto') {
            steps {
                echo 'Descargando dependencias del pom.xml y compilando el código...'
                // Descarga dependencias de Maven y compila sin correr las pruebas aún
                sh 'mvn clean compile'
            }
        }

        stage('Ejecutar Pruebas') {
            steps {
                echo 'Ejecutando la suite de pruebas automatizadas (TestNG/JUnit)...'
                // Ejecuta los tests configurados en el proyecto Java
                sh 'mvn test'
            }
        }
    }

    // Esta sección siempre se ejecuta al final, fallen o pasen las pruebas
    post {
        always {
            echo 'Finalizó la ejecución.'

            // Más adelante, para generar el reporte de Allure (si lo usas en tu pom.xml),
            // descomentarás esta línea:
            // allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]

            echo 'Limpiando espacio de trabajo para no saturar el disco del servidor...'
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