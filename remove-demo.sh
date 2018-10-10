#!/usr/bin/env bash

set -e

rm -r ./src/main/kotlin/app/quiz/ || true
rm -r ./src/test/kotlin/app/quiz/ || true
rm -r ./src/test/kotlin/featuretests/quiz/ || true
rm -r ./src/main/resources/db/migration/V3__Add_quizzes_table.sql || true
rm -r ./src/main/resources/templates/quizzes/ || true

deleteLineContaining() {
    local file_name="${1}"
    local match="${2}"
    local line_number=$(grep -n "${match}" "${file_name}" | awk -F ":" '{print $1}')
    if [[ ! -z "${line_number}" ]]; then
        sed -i.bak -e "${line_number}d" "${file_name}"
    fi
}

deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "quiz.QuizService"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "quiz.NewQuizPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "quiz.QuizEditPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "quiz.QuizListPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "delete from quizzes"
deleteLineContaining ./src/test/kotlin/app/auth/user/UserRepositoryTest.kt "delete from quizzes"

deleteLineContainingWithAnnotation() {
    local file_name="${1}"
    local match="${2}"
    local line_number=$(grep -n "${match}" "${file_name}" | awk -F ":" '{print $1}')
    local annotation_line_number=$((line_number-1))
    if [[ ! -z "${line_number}" ]]; then
        sed -i.bak -e "${annotation_line_number}d;${line_number}d" "${file_name}"
    fi
}

deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "quizListPage: QuizListPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "newQuizPage: NewQuizPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "quizEditPage: QuizEditPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "quizService: QuizService"

rm ./src/test/kotlin/helpers/FeatureTest.kt.bak || true
rm ./src/test/kotlin/app/auth/user/UserRepositoryTest.kt.bak || true

./gradlew clean