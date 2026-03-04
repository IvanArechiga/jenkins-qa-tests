properties([
    parameters([
        [$class: 'ChoiceParameter',
            choiceType: 'PT_CHECKBOX',
            description: 'Selecciona módulos ADICIONALES a ejecutar en paralelo:',
            name: 'MODULOS_ADICIONALES',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [sandbox: false, script: 'return ["Esperando aprobación de seguridad..."]'],
                script: [sandbox: false, script: '''
                    try {
                        def jenkinsInstance = jenkins.model.Jenkins.get()

                        def currentJobName = binding.hasVariable('jenkinsProject') ? jenkinsProject.fullName : ""

                        return jenkinsInstance.getAllItems(hudson.model.Job.class).findAll { job ->
                            job.fullName.toString().trim() != currentJobName
                        }.collect { it.fullName }.sort()
                    } catch (Exception e) {
                        return ["Error: " + e.getMessage()]
                    }
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
        stage('Limpieza Profunda') {
            steps {
                deleteDir()
                sh "rm -rf build/ .gradle/ allure-results/"
                checkout scm
            }
        }

        stage('Ejecución de Pruebas') {
            steps {
                script {
                    def tareasParalelas = [:]

                    tareasParalelas["Pruebas Core"] = {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            sh "gradle clean test --no-daemon --rerun-tasks --info --no-configuration-cache"
                        }
                    }

                    if (params.MODULOS_ADICIONALES) {
                        def seleccionados = params.MODULOS_ADICIONALES.split(',')
                        seleccionados.each { nombre ->
                            def jobName = nombre.trim()

                            if (jobName && jobName != env.JOB_NAME && !jobName.contains("Esperando") && !jobName.contains("Error")) {
                                tareasParalelas["Extra: ${jobName}"] = {

                                    build job: jobName, wait: true, parameters: [
                                        string(name: 'MODULOS_ADICIONALES', value: '')
                                    ]
                                }
                            }
                        }
                    }

                    parallel tareasParalelas
                }
            }
        }
    }

    post {
        always {
            script {
                echo "1. Verificando y comprimiendo resultados crudos de Allure..."

                // Script robusto: busca la carpeta, y si no existe, crea un ZIP vacío para no romper el pipeline
                sh '''
                    if [ -d "build/allure-results" ]; then
                        echo "Resultados encontrados en build/allure-results"
                        cd build/allure-results && zip -r ../../results.zip * || echo "Carpeta vacía"
                    elif [ -d "allure-results" ]; then
                        echo "Resultados encontrados en allure-results"
                        cd allure-results && zip -r ../results.zip * || echo "Carpeta vacía"
                    else
                        echo "⚠️ No se encontraron resultados de Allure (posible error de compilación)."
                        mkdir -p allure-results
                        echo "No data generated" > allure-results/empty.txt
                        cd allure-results && zip -r ../results.zip *
                    fi
                '''

                // Extraemos el nombre del proyecto dinámicamente
                def projectName = binding.variables['JOB_NAME']?.toString()?.split('/')?.last() ?: "Default-Project"

                echo "2. Enviando métricas al Servidor Centralizado de Allure para el proyecto: ${projectName}"

                // 🚨 IMPORTANTE: Reemplaza "TU_IP_AQUI" con la IP real de la máquina donde corre el docker de Allure
                def IP_SERVIDOR_ALLURE = "192.168.1.XX" // <-- ACTUALIZA ESTO
                def allureApiUrl = "http://${IP_SERVIDOR_ALLURE}:5050/allure-docker-service/send-results?project_id=${projectName}"

                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    // Enviamos el ZIP a la API de nuestro Allure Docker Service
                    sh """
                        curl -X POST '${allureApiUrl}' \
                             -H 'Content-Type: multipart/form-data' \
                             -F 'allureResults=@results.zip'
                    """

                    // Le decimos al servidor que genere y actualice el reporte HTML con el nuevo historial
                    sh "curl -X GET 'http://${IP_SERVIDOR_ALLURE}:5050/allure-docker-service/generate-report?project_id=${projectName}'"
                }

                echo "✅ Resultados enviados al Hub de Métricas."
            }
            // Limpieza del servidor Jenkins para ahorrar espacio
            cleanWs()
        }
    }
}