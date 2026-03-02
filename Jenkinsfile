// MAGIA AÑADIDA: Interfaz de Checkboxes Segura (Active Choices Plugin)
properties([
    parameters([
        [$class: 'ChoiceParameter',
            choiceType: 'PT_CHECKBOX',
            description: 'Selecciona los módulos a ejecutar en paralelo (El Core siempre se ejecuta):',
            name: 'REPOSITORIOS_A_EJECUTAR',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [sandbox: true, script: 'return ["Error al cargar repositorios"]'],
                // Aquí pones tu lista. Nota: añadir ':selected' al final marca la casilla por defecto.
                script: [sandbox: true, script: 'return ["core:selected", "ventas", "cajas", "inventario", "pagos"]']
            ]
        ]
    ])
])

pipeline {
    agent any

    tools {
        jdk 'JDK21'
        gradle 'Gradle'
    }

    stages {
        stage('Descargar Código') {
            steps {
                checkout scm
                echo 'Código base descargado correctamente.'
            }
        }

        stage('Compilar Proyecto') {
            steps {
                sh 'gradle clean testClasses'
            }
        }

        // Ejecución 100% Dinámica basada en los Checkboxes seleccionados
        stage('Ejecución de Pruebas en Paralelo') {
            steps {
                script {
                    // Limpiamos los resultados recibidos (Jenkins envía las respuestas separadas por coma)
                    def seleccionBruta = params.REPOSITORIOS_A_EJECUTAR ?: "core"
                    def seleccionados = seleccionBruta.split(',')
                    def tareasParalelas = [:]

                    for (int i = 0; i < seleccionados.size(); i++) {
                        // Limpiamos la palabra en caso de que venga con el texto extra ':selected'
                        def nombreRepo = seleccionados[i].replace(':selected', '').trim()

                        if (nombreRepo != "") {
                            tareasParalelas["Pruebas ${nombreRepo}"] = {
                                stage("Repo: ${nombreRepo}") {
                                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                        echo "Iniciando pruebas para el repositorio: ${nombreRepo}..."

                                        // Comando de ejecución dinámico de Gradle
                                        sh "gradle test --tests '*${nombreRepo}*' --rerun-tasks --info --no-configuration-cache"
                                    }
                                }
                            }
                        }
                    }

                    if (tareasParalelas.size() > 0) {
                        echo "Ejecutando simultáneamente: ${seleccionBruta}"
                        parallel tareasParalelas
                    } else {
                        echo "No se seleccionó ningún repositorio."
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Generando Reporte Allure Consolidado...'
            allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            cleanWs()
        }
    }
}