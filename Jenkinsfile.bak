pipeline {
    agent any // Se ejecuta en el contenedor principal de Jenkins

    // INYECCIÓN DE HERRAMIENTAS: Aquí Jenkins carga Java y Gradle
    // Los nombres deben coincidir EXACTAMENTE con los que pusiste en el Paso 2.1
    tools {
        jdk 'JDK21'
        gradle 'Gradle'
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
                echo 'Descargando dependencias de Gradle y compilando clases de prueba...'
                // Prepara el proyecto descargando dependencias
                sh 'gradle clean testClasses'
            }
        }

        stage('Ejecutar Pruebas') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    // Añadimos --no-configuration-cache para evitar errores de compatibilidad en Gradle 8+
                    sh 'gradle test --rerun-tasks --info --no-configuration-cache'
                }
            }
        }
    }

    // Esta sección siempre se ejecuta al final, fallen o pasen las pruebas
    post {
        always {
            echo 'Finalizó la ejecución.'

            // Más adelante, para generar el reporte de Allure en Gradle,
            // descomentarás esta línea (Nota: en Gradle la carpeta por defecto es build/allure-results):
            allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]

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