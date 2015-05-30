/**
 * Created by NotePad on 30.05.2015.
 */

$("#log_in").on('click',sendLogin);
$("#register").on('click',sendRegister);

function sendLogin(){
    var user = $("#username").val();
    var pass = $("#password").val();
    if (isMessageValid(user) && isMessageValid(pass)){
        var xhr = new XMLHttpRequest();
        xhr.open("post","/login",true);
        xhr.send(JSON.stringify({user : user, password : pass}));
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                if (xhr.status == "200") {
                    if(typeof(Storage) == "undefined") {
                        console.log('localStorage is not accessible');
                        return;
                    }
                    localStorage.setItem("user", user);
                    localStorage.setItem("username", user);
                    location.replace(xhr.responseText);
                }
                else {
                    $("#info").text(xhr.responseText);
                    console.log('login error');
                }
            }
        };
    }
}
function sendRegister(){
    var user = $("#username").val();
    var pass = $("#password").val();
    if (isMessageValid(user) && isMessageValid(pass)){
        var xhr = new XMLHttpRequest();
        xhr.open("post","/register",true);
        xhr.send(JSON.stringify({user : user, password : pass}));
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                if (xhr.status == "200") {
                    $("#info").text(xhr.responseText);
                }
                else {
                    if (xhr.status == "302") {
                         $("#info").text(xhr.responseText);
                } else {
                    console.log("register error");
                     }
                }
            }
        };
    }
}

var isMessageValid = function(messageText){
    if (messageText == "")
        return false;
    if (messageText.length == 0 )
        return false;
    for (var i = 0, ch; i < messageText.length; i++ ){
        ch = messageText.charCodeAt(i);
        if(ch != 32 && ch != 10){
            return true;
        }
    }
    return false;
};