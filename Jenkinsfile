// MAGIA AÑADIDA: Lectura dinámica de la API interna de Jenkins (Sin Sandbox)
properties([
    parameters([
        [$class: 'ChoiceParameter',
            choiceType: 'PT_CHECKBOX',
            description: 'Selecciona módulos ADICIONALES a ejecutar en paralelo (El proyecto principal siempre se ejecutará automáticamente):',
            name: 'MODULOS_ADICIONALES',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [sandbox: false, script: 'return ["Error al cargar módulos"]'],
                // Al apagar el sandbox (sandbox: false), Jenkins nos permite consultar su núcleo.
                // Buscamos todos los Jobs y los metemos a la lista dinámicamente.
                script: [sandbox: false, script: '''
                    def listaProyectos = []

                    // Consultamos el núcleo de Jenkins por todos los elementos (Jobs/Proyectos)
                    jenkins.model.Jenkins.instance.getAllItems(hudson.model.Job.class).each { job ->
                        // Evitamos poner el proyecto actual en la lista de extras para no duplicar
                        if (!job.fullName.contains("QA-Automatizacion-Core")) {
                            listaProyectos.add(job.fullName)
                        }
                    }
                    return listaProyectos
                ''']
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

                    // 2. TAREAS ADICIONALES: Solo si el usuario marcó algún checkbox válido
                    def seleccionBruta = params.MODULOS_ADICIONALES ?: ""

                    if (seleccionBruta != "" && seleccionBruta != "Error al cargar módulos") {
                        def seleccionados = seleccionBruta.split(',')

                        for (int i = 0; i < seleccionados.size(); i++) {
                            def nombreModulo = seleccionados[i].trim()

                            if (nombreModulo != "") {
                                tareasParalelas["Módulo Extra: ${nombreModulo}"] = {
                                    stage("Extra: ${nombreModulo}") {
                                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                            echo "Iniciando pruebas adicionales para el proyecto de Jenkins: ${nombreModulo}..."

                                            // Aquí usamos el nombre del proyecto detectado en Jenkins para filtrarlo en Gradle
                                            sh "gradle test --tests '*${nombreModulo}*' --rerun-tasks --info --no-configuration-cache"

                                            /* Nota de Arquitecto: Si los checkboxes detectados son realmente OTROS pipelines de Jenkins,
                                               en lugar del 'sh' de arriba, deberías usar el disparador de jobs nativo:
                                               build job: "${nombreModulo}", wait: true
                                            */
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Ejecutamos el bloque principal y los extras (si los hay) simultáneamente
                    echo "Ejecutando en paralelo: Proyecto Principal + Extras Seleccionados"
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