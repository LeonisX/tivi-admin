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

## По видео

Пока сделана пробная версия добавления нового видео.

Идея такая - сразу запрашиваем ссылку на видео и вытаскиваем максимальное число данных без участия пользователя.

Так же вытаскиваю все возможные картинки

Что не сделано:
* Комменты к видео - их было бы неплохо засунуть в META
* META - научиться вычислять верные ключевые слова, добавлять какие-то свои
* Заливка своей картинки
* Обработка картинок
* Несколько вариантов сжатия (палитра, png, jpg)
* Корректная обработка не-ютуба
* Корректная с расширениями
* toggles to radiobuttons

Что сломано:
* PHP возвращает список ошибок, но они почему-то не видны

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

Изучить:

https://javagraphics.java.net/
http://javagraphics.blogspot.md/2014/02/images-color-palette-reduction.html

https://www.idrsolutions.com/jdeli/download
https://blog.idrsolutions.com/2015/07/how-to-generate-smaller-png-files-in-java/
https://blog.idrsolutions.com/2015/04/how-to-write-jpeg-images-in-java/
https://blog.idrsolutions.com/2015/04/how-to-write-out-png-images-in-java-2/

http://www.java2s.com/Code/Java/2D-Graphics-GUI/Providesusefulmethodsforconvertingimagesfromonecolourdepthtoanother.htm

