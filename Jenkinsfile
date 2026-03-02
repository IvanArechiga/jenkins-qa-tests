// MAGIA AÑADIDA: Interfaz de Checkboxes Segura (Active Choices Plugin)
properties([
    parameters([
        [$class: 'ChoiceParameter',
            choiceType: 'PT_CHECKBOX',
            description: 'Selecciona módulos ADICIONALES a ejecutar en paralelo (El proyecto principal siempre se ejecutará automáticamente):',
            name: 'MODULOS_ADICIONALES',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [sandbox: true, script: 'return ["Error al cargar módulos"]'],
                // Esta es la lista de módulos extra. Ninguno está marcado por defecto.
                script: [sandbox: true, script: 'return ["ventas", "cajas", "inventario", "pagos"]']
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

        // Ejecución 100% Dinámica: Fija el proyecto base y añade los extras seleccionados
        stage('Ejecución de Pruebas en Paralelo') {
            steps {
                script {
                    def tareasParalelas = [:]

                    // 1. TAREA OBLIGATORIA: El proyecto principal siempre se ejecuta
                    tareasParalelas["Proyecto Principal"] = {
                        stage("Proyecto Principal") {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                echo "Iniciando pruebas base obligatorias..."
                                sh "gradle test --rerun-tasks --info --no-configuration-cache"
                            }
                        }
                    }

                    // 2. TAREAS ADICIONALES: Solo si el usuario marcó algún checkbox
                    def seleccionBruta = params.MODULOS_ADICIONALES ?: ""

                    if (seleccionBruta != "" && seleccionBruta != "Error al cargar módulos") {
                        def seleccionados = seleccionBruta.split(',')

                        for (int i = 0; i < seleccionados.size(); i++) {
                            def nombreModulo = seleccionados[i].trim()

                            if (nombreModulo != "") {
                                tareasParalelas["Módulo Extra: ${nombreModulo}"] = {
                                    stage("Extra: ${nombreModulo}") {
                                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                            echo "Iniciando pruebas adicionales para: ${nombreModulo}..."
                                            sh "gradle test --tests '*${nombreModulo}*' --rerun-tasks --info --no-configuration-cache"
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Ejecutamos el bloque principal y los extras (si los hay) simultáneamente
                    echo "Ejecutando en paralelo: Proyecto Principal + [${seleccionBruta}]"
                    parallel tareasParalelas
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