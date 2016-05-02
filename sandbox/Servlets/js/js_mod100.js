<script language="javascript"> 
<!-- //JavaScript idiom for hiding script from non-compliant browsers

function CheckEmail(email)
{
var firstchunk,ind,secondchunk

if (email == "")
{
return false
}

//get the number of chars before the "@" character
ind = email.indexOf("@")

//if the string does not contain an @ then then return true
if (ind == -1 ){

return false
}

//if the first part of email is less than 2 chars and second part < 6 chars then reject
firstchunk = email.substr(0,ind)
secondchunk = email.substr((ind + 1),(email.length + 1))

if ((firstchunk.length < 2 ) || (secondchunk.length < 7) || (secondchunk.indexOf(".") == -1)){ 
return false
}

//the email was okay; at least it had a @, more than 1 username chars,
//more than 6 chars after the "@", and the substring after the "@" contained
//a "."
return false
}

}
function createWindow(uri) {
  var newWin = window.open(uri,'newwin1','width=500,height=400,resizable,scrollable,scrollbars=yes');
  newWin.focus();
} 


-->
</script>