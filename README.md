# TiVi-Admin

Этот проект признан заменить админку сайта [tv-games.ru]() и сделать администрирование увлекательным занятием.

Зачем это понадобилось?

В первую очередь очень напрягает добавлять новые видео. В частности, необходимо:

1. Скопипастить название
2. Организовать ЧПУ-замену
3. Выставить дату публикации. Очень напрягает листать календарь
4. Мета-описание, ключевые слова
5. Возрастной ценз - ну, это не напрягает, ладно :)
6. URL ролика - для Ютуба надо вычленить какую-то часть, для ВК определённая строка - всё это несколько напрягает
7. Вводный и полный текст - опять же, копипаст либо писанина от себя
8. Автор:
  - Имя
  - Сайт
  - Почта
9. Статистика (не трогаем)
10. Изображение - его приходится генерировать, обрабатывать, заливать по FTP и прочее - крайне обременительный этап.
11. Состояние - включено по умочанию.
12. Доступность - всем или только зарегистрированным.
12. Какие-то лишние поля, отвлекающие внимание.

В базе данных есть место для картинки (OpenGraph) и другой информации

Итак, что я думаю.

1. На первых порах, пока не разберусь с авторизацией и прочим, будет необходимо прятать эту админку.
2. Скопирую компоновку нынешней админки, она вполне работающая.

## По книгам

У меня есть база данных по околоигровым книгам и журналам. Чтобы не изобретать велосипед - собрана она была в Calibre.
Движок не идеальный в некоторых отношениях, но, пока из бесплатного лучшего я не встречал.

На сайте уже несколько лет ведётся сбор подобных книг, и возникла идея автоматизировать процесс добавления книг.

Движок сайта + Calibre позволяют сделать нечто интересное.

Книги сортируются по платформам. Есть книги и журналы, в которых описано несколько платформ. Если есть одна
доминирующая платформа, книга будет лежать в ней. Если их несколько, то в `console` или в `computers`.

В остальных платформах будет отдельная тема `Упоминание в:`...

Есть возможность для каждой платформы добавить подкатегории, например: `Описания и прохождения`, `мануалы`,
`программирование и ремонт`, возможно `журналы`, что-то ещё.
solutions, manuals???, docs, programming, ???

TODO cat - add/update
TODO dump (backup)
TODO compare with dump, show diff
TODO restore
TODO wise error processing / specialized http client

DUMP: трудность - менять кодировку в соответствии с таблицей
DUMP TODO: многопоточность, повтор запроса в случае ошибки
TODO DUMP BLOB (HEX OR NOT) - с сервера идут уже корявые данные. в идеале - отказаться от конвертации кодировки и отправлять как есть. и да - нужно слать кодировку для базы данных
но это далеко идущие  планы. Полный дамп не удаётся из-за блобов, поэтому, перед работой дампить двумя дамперами для верности.

TODO по-прежнему - дампить базу над которой работаю

1. Сравниловка - сравнивать две базы калибре, выводть разницу.
Есть внутренние списки - для них нужно ручное форматирование.
2. Результат сравнения: Map<CalibreBook, Pair<String, String>>
3. Выводить:
> 40 игр 3DO:\
>     \- title: 40 игр 3DO -> 41 игра 3DO
4. То же самое для сайта
5. Дамп баз сайта
6. Глобальный тест медиа, категорий - дамп, на сайт, в файл, CRUD,...

Итак, задачи:

- [x]Вытаскивать данные из Calibre
- [x] Аудит + фикс
- Вытаскивать данные с сайта
- Сличать, находить разницу
- Обновлять на сайте

Чистить описание от лишнего

Добавить нужные подкатегории на сайт + их поиск. Возможно подумать про автоматическое добавление таких подкатегорий

Добавить необязательные теги

Дампить в Json и обратно

Находить различия между редакциями базы


## По видео

Реализован полный CRUD по видео.

Идея такая - сразу запрашиваем ссылку на видео и вытаскиваем максимальное число данных без участия пользователя.

Так же вытаскиваю все возможные картинки

Что надо сделать:
* теги

Баги:
* После редактирования видео сбрасывается количество видео на страницу, хотя, отображаются все.
* Почему-то показывается лишняя страница
* После выбора категории не пересчитывается число страниц и не позиционируется на первую
* Сортировка по дате (Emugamer TV) работает неверно
* Очень не нравится ограничение на длину строки. Как-то предлагать обходить его. 
* Если в тексте кавычки и другие специальные знаки, то они отображаются как &amp;#039;
* Игровой Историк - при показе по 10 две лишние страницы

Что не сделано:
* Комменты к видео - их было бы неплохо засунуть в META
* META - научиться вычислять верные ключевые слова, добавлять какие-то свои
* Заливка своей картинки
* Обработка картинок
* Несколько вариантов сжатия (палитра, png, jpg)
* Корректная обработка не-ютуба
* Корректная работа с расширениями
* Перейти от toggles к radiobuttons
* Пакетное раскидывание видео по дням
* Для видео следует хранить две даты - создания (для истории) и показа (для плеера)
* Кнопки стирания мета данных
* Кнопки индивидуального обновления метаданных
* При превышениии длины хотя бы показывать на сколько

2. Список видео (с возможностью сортировки):
  * ID
  * Название/категория (вероятно предпросмотр картинки)
  * Опубликовано
  * Просмотров
  * Комментарии
  * Управление (Включение и выключение видео, предпросмотр, удаление, редактирование)
  * Отметить
  * В верху количество на страницу, категории

Этого уже много, большее, например, редактирование категорий пока не планирую

## Сборка, запуск из командной строки

Поскольку с запуском в IDE проблем не возникает (например, в IntelliJ IDEA это правый клик по файлу MainApp, дальше Run..., или Ctrl+Shift+F10), опишу, как это делается в командной строке.

* Сборка: `sbt package`
* Запуск (Linux): `./run.sh`
* Запуск (Windows): `./run.bat`


У sbt есть ещё несколько полезных ключей: `clean`, `compile`, `run`, и т.д.

## Изучить:

https://javagraphics.java.net/
http://javagraphics.blogspot.md/2014/02/images-color-palette-reduction.html

https://www.idrsolutions.com/jdeli/download
https://blog.idrsolutions.com/2015/07/how-to-generate-smaller-png-files-in-java/
https://blog.idrsolutions.com/2015/04/how-to-write-jpeg-images-in-java/
https://blog.idrsolutions.com/2015/04/how-to-write-out-png-images-in-java-2/

http://www.java2s.com/Code/Java/2D-Graphics-GUI/Providesusefulmethodsforconvertingimagesfromonecolourdepthtoanother.htm

