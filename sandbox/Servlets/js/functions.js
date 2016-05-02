<script language="JavaScript"> 

function CheckEmail(email, name)
{
    var firstchunk, indx, secondchunk

	if (name == ""){
        alert("Пожалуйста, введите своё имя.")

        return false
    }
    
    if (email == ""){
        alert("Пожалуйста, введите e-mail адрес.")

        return false
    }

    // получаем индекс символа "@"
    indx = email.indexOf("@")

    // если в строке нет символа "@" - возвращаем false
    if (indx == -1 ){

        alert("Пожалуйста, введите верный e-mail адрес.")

        return false
    }

    // если первая часть email < 2 символов и вторая часть < 7 символов
    //(нестрогий но действенный критерий) - отклонить введённый адрес

    firstchunk = email.substr(0, indx) // до "@", не включая его

    // от "@" и до конца адреса
    secondchunk = email.substr(indx + 1) 

    // если часть после "@" не содержит точку "." то вернуть false
    if ((firstchunk.length < 2 ) || (secondchunk.length < 7) ||
    (secondchunk.indexOf(".") == -1)){ 
    	alert("Пожалуйста, введите верный e-mail адрес.")
    	return false
	}

return true

}//CheckEmail

function CreateWindow(uri) {

    var newWin = window.open(uri,'newwin1','width=500,height=400,resizable,scrollable,scrollbars=yes');
    newWin.focus();

} 

</script>