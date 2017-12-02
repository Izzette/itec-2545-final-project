" final_project.vim

let project_base = fnamemodify(resolve(expand('<sfile>:p')), ':h')

let g:syntastic_java_javac_classpath =
  \ project_base . 'src/main/java' . ':' .
  \ project_base . 'src/test/java'
let g:JavaComplete_PomPath = project_base . '/pom.xml'

" vim: set ts=2 sw=2 et syn=vim:
