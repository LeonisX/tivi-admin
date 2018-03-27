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

Сейчас задача номер один - использовать всю доступную информацию для генерации.

1. [x] Теги - ключевых словах, в кратком и полном виде. Брать только текущую платформу.
2. [x] Теги - в тексте
3. [x] Дополнительные теги в тексте
4. [x] Страницы упоминаний (альт теги) nes_citation
5. [x] Страницы книг в поиске nes_search
6. [x] Страницы со ссылками на журналы (альт теги) nes_magazines
7. [ ] Выводить журналы - все на одной странице с картинками. Даты и данные выводить, издателя - только если меняется
8. [x] Дампить в подкатегории (серии). Корректировать ссылки на них

Общий ход работ:

- [x] Вытаскивать данные из Calibre
- [x] Аудит + фикс
- [x] Вытаскивать данные с сайта
- [x] Сличать, находить разницу
- [x] Собирать из Calibre файлы в отдельную категорию
 - [x] images
 - [x] files
 - [x] unzip/unrar
- [ ] Обновлять на сайте 60%
 - [x] added
 - [x] updated
 - [x] deleted
 - [ ] images
 - [ ] files

Файлы пока можно заливать вручную.


Обновлялка на сайте

Новое:
- [x] комиксы [comix] - на них делать ссылки как в упоминаниях журналов
- [x] документация по железу+программированию [doc] - собирать как солюшены
- [x] документация по эмуляторам [emulator] - собирать как солюшены, но это будут ссылки
- [x] описания и похождения [guide] - собирать как солюшены
- [x] мануалы консолей, включая сервисные [manual] - собирать как журналы
- [?] каталоги? тут только каталоги от спекки пока. возможно, не стоит выделять. или стоит??

manual
guide
instruction
solution
walkthrough
documentation

### Задачи

#### База данных

- имена файлов на уникальность
- подумать как грамотно оформлять журналы - из первого берётся описания для краткого текста, из остальных только для основного текста 
- добавить GD в базу
- прочесать журналы с http://tehnoarhiv.ru
- ко всем гайдам дать обложки игр либо арт
- добавить в гайды все материалы из дневников и форума
- описания игр могут подходить к нескольким платформам
- рейтинги обратно в calibre (when update)
- высший пилотаж - перелинковать книги где необходимо (например, 50 игр для 3DO), волшебный мир, итд.
- постараться всех авторов засунуть в авторы
- шерстить cbr на наличие tiff проверить порядок
- manual clean cpus, add new books, magazines, comics.
- total audit

#### Сайт

- Пересчитать рубрики, пересчитать категории
- проверить ссылки на скачивание
- полностью проверить весь генерируемый контент
- генерить страницы всех гайдов для форума
- SEO - Загонять в EXIF максимально инфу по книгам Apache commons Imaging https://stackoverflow.com/questions/36868013/editing-jpeg-exif-data-with-java
- генерить новостные анонсы по изменениям
- SEO - Open Graph - подумать.
- SEO - у всех искусственных страниц продумать описания и ключевые слова

#### Код

Не удалось внедрить избранные книги.
Так же сломан поиск имён файлов с учётом хвоста.
Если этот функционал не будет нужен - выпилить всё что касается списков файлов (BookRecord, ...)


[v] в коде, если книга, журнал или комикс - хардкодить ссылку на внешний источник.
[ ] вероятно кастомизировать кнопку скачивания, если ссылка

[test] для остальных документов предусмотреть вывод на скачку остальных файлов.
[test] порядок - оригинальный (DATA ID, а в ррхивах по порядку следования).

[ ] у DATA ещё один филд - tail. при поиске уникального имени файла прибавлять его.
[ ] если что - на будущее сюда можно засунуть другие поля, например, тот же CRC32.

[ ] дальше, прочесать полный дамп базы данных и для всех Alt дать свои хвосты.


-- //TODO
-- - трудность - при генерации ссылок на файлы нельзя однозначно идентифицировать по размеру.
-- решение:
-- 1. У каждой книги есть UUID - выкладывать его после #
-- 2. При чтении списка книг заносить этот ID в объект, так же название книги
-- 3. При фильтрации файлов фильтровать сначала по UUID, потом уже искать по размеру. Отпадает необходимость в CRC32




- поскольку места на сайте не хватает - залить всё в облако и предоставить доступ.
- солюшены, эмуляторы, доки, мануалы пока держать на сайте

- Всё ещё думать про журналы. Может ещё один блок для описания серии???

- Comics - если не группированы то должна быть стандартная кнопка скачивания.

- Рендерер - разбить на маленькие процедурки, унифицировать код.

- magazines - если больше определённого количества - оформлять как категорию.
возможно, стоит ввести ещё один флаг в калибре. или прописывать в тегах. например, группировать журналы или генерить по-одному

-? magazines - если группированы на странице то выводить все данные в книжном виде.

- magazines - есть мысль выводить журналы в поиске со всеми журналами

- magazines - разыскиваемые - выводить картинки???

- копирование файлов - логировать в окно, показывать прогресс. то же самое с картинками

- compare - одного сравнения базы Calibre мало. надо проходиться по файлам. Например смена обложки в базе не регистрируется, только дата изменения.
Для ускорения - сканить файлы только изменённых книг.
Но для полного аудита надо уметь сканить всё.

- audit - magazines without serie

- cpu - искать вместе с платформой (уникальность)

- wise error processing / specialized http client

DUMP: трудность - менять кодировку в соответствии с таблицей. Вероятно, спасает переход на чистый UTF-8
DUMP TODO: многопоточность
TODO DUMP BLOB (HEX OR NOT) - с сервера идут уже корявые данные. в идеале - отказаться от конвертации кодировки и отправлять как есть. и да - нужно слать кодировку для базы данных
но это далеко идущие  планы. Полный дамп не удаётся из-за блобов, поэтому, перед работой дампить двумя дамперами для верности.


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

