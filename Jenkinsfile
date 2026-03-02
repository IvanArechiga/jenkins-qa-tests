pipeline {
    agent any // Se ejecuta en el contenedor de Jenkins

    // Herramientas necesarias (ej. si usas NodeJS para Cypress o Playwright)
    // tools { nodejs 'NodeJS' }

    stages {
        stage('Descargar Código') {
            steps {
                checkout scm
                echo 'Código descargado de GitHub correctamente.'
            }
        }

        stage('Instalar Dependencias de Pruebas') {
            steps {
                echo 'Instalando librerías (ej. npm install o pip install)...'
                // sh 'npm install'  <-- Descomenta esto en la vida real
            }
        }

        // AQUÍ ESTÁ LA MAGIA QUE PEDISTE: EJECUCIÓN SIMULTÁNEA (PARALELA)
        stage('Ejecución de Pruebas Core') {
            parallel {
                stage('Módulo: Cajas') {
                    steps {
                        echo 'Iniciando pruebas automatizadas de CAJAS...'
                        // sh 'npm run test:cajas' <-- Tu comando real de test
                        // Simulamos que toma tiempo
                        sleep 5
                    }
                }
                stage('Módulo: Ventas') {
                    steps {
                        echo 'Iniciando pruebas automatizadas de VENTAS...'
                        // sh 'npm run test:ventas' <-- Tu comando real de test
                        sleep 5
                    }
                }
            }
        }
    }

    // Esta sección siempre se ejecuta al final, fallen o pasen las pruebas
    post {
        always {
            echo 'Generando Reporte de Resultados...'
            // Suponiendo que tus pruebas generan resultados en la carpeta allure-results
            // allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]

            echo 'Limpiando espacio de trabajo...'
            cleanWs()
        }
        success {
            echo '✅ Todas las pruebas pasaron exitosamente. El sistema es estable.'
            // Aquí podrías poner un código para mandar mensaje a Slack o Teams
        }
        failure {
            echo '❌ Algunas pruebas fallaron. Revisar el reporte Allure de inmediato.'
        }
    }
}