<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Отчёт об изменениях</title>
</head>
<body>
<h2>Общая статистика</h2>
<p>За период с ${fromDate} по ${toDate} было добавлено, либо отредактировано ${editedCount} ${editedRecordsString}.
    <#if deletedCount != 0> Удалено дубликатов: ${deletedCount}.</#if> Оцифровано, либо найдено ${totalAddedScans} ${totalAddedPlural}.</p>

<p>Всего в базе данных ${totalRecords} ${totalRecordsString}, а именно:</p>

<ul>
    <#list changelog as item>
    <li>${item.title}: ${item.count}<#if item.diff != 0> (+${item.diff})<#else></#if></li>
    </#list>
</ul>

<p>В электронном виде доступно ${availableBooks}% от всей коллекции.</p>
<p>Осталось отсканировать всего ${absentBooks} ${absentBooksPlural}.</p>

<h2>Основные источники новых поступлений</h2>

<#list sources as item>
<b><#if item.uri ?has_content><a href="${item.uri}">${item.title}</a><#else>${item.title}</#if></b><#if item.names ?has_content> (${item.names})<#else></#if>
<ul>
    <#list item.books as it>
    <li><#if it.cpu ?has_content><a href="${it.siteUri}">${it.title}</a><#else>${it.title}</#if></li>
    </#list>
</ul>
</#list>

<h2>Статистика по платформам</h2>

<ul>
    <#list byPlatform as item>
    <li>+${item.count} ${item.book} <#if item.uri ?has_content><a href="${item.uri}">${item.title}</a><#else>${item.title}</#if></li>
    </#list>
</ul>

<h2>Новые и обновлённые книги в картинках</h2>

<p><i>Картинки кликабельны.</i></p>

<#list byPictures as item>
    <a href="${item.siteUri}"><img src="${item.siteThumbUri}" title="${item.title}" alt="${item.cpu}"/></a>
</#list>

</body>
</html>