//UI LOGIC


// ----------- VARS

var chatView;  
var messageList = [];
var editMode = false;
var settingsMode = false;
var currentUserName;
var currentUser;

// Struct for adding message to UI
var newMessage = function (date, username, user, messageId, messageText){
    return {
        date: date,
        username: username,
        user: user,
        messageId: messageId,
        messageText: messageText
    };
};

// Struct for sending message to server
var newMessageSendRequest = function (username, user, messageText){

    return  JSON.stringify({
            username: username,
            user: user,
            messageText: messageText,
            status: "new"
        });
};
// Struct for deleting message from server
var deleteMessageRequest = function (username, user, messageId){
    return  JSON.stringify( {
            messageId: String(messageId),
            username: username,
            user: user,
            status: "delete"
        });
};

var editMessageRequest = function (username, user, messageId, messageText){
    return  JSON.stringify( {
            messageText: messageText,
            messageId: String(messageId),
            username: username,
            user: user,
            status: "edit"
        });
};

// ----------- Main Function

$( document ).ready(function(){
    handleChatSize();
    $( window ).resize(handleChatSize);
    chatView = $('.panel-body');
    scrollToBottom(chatView,false);
    chatView.css("opacity","100");
    $("#MessageForm").focus();
    //NewMessageAdd
    $("#SendBtn").click(userMessageSended);
    registerKeyPress(editMode);
//Additional setup
    $("#EditBtn").click(editMessageEditMode);
    $("#CancelEditBtn").click(cancelEditMode);
    
//SettingsListeners    
    $('.SettingsButton').on('click', toggleSettings);
    $("#NameSettingsApply").on('click', applySettings);
    $("#CancelSettingsBtn").on('click', toggleSettings);
    $("#LogoutBtn").on('click',sendLogout);

    run();
});

// ----------- LOGIC. Chat UI s. Server Requests

function run() { // start of app. data restore.
    
    restore() ;
    checkForEditing(currentUserName);
    getAction();
    longPolling();
}

function longPolling() {
    var xhr = new XMLHttpRequest();
    xhr.open("get","/chat",true);
    xhr.timeout = 3000000;
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
                getActionFromServerWithJSON(xhr.responseText);
            }
            else {
                console.log('get message error');
            }
            longPolling();
        }
    };
}

function restore() {
	if(typeof(Storage) == "undefined") {
		console.log('localStorage is not accessible');
		return;
	}
    currentUser = localStorage.getItem("user");
    var userNameopt = localStorage.getItem("username");
        currentUserName = userNameopt;
        $("#NameForm").val(currentUserName);
}

function store(){
    if(typeof(Storage) == "undefined") {
		console.log('localStorage is not accessible');
		return;
	}

    localStorage.setItem("username", currentUserName);
}

function deleteFromList(messageId){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageId == messageId){
            messageList.splice(i,1);
        }
    }
}

function editInList(messageId,newText){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageId == messageId){
            messageList[i].messageText = newText;
        }
    }
}

// Test Func. It works.
function postNewMessage(data){
    var xhr = new XMLHttpRequest();
    xhr.open("post","/chat",true);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.send(data);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
            }
            else {
                console.log('get message error');
            }
        }
    };
}

function postDeleteMessage(data){
    var xhr = new XMLHttpRequest();
    xhr.open("post","/chat",true);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.send(data);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
            }
            else {
                console.log('get message error');
            }
        }
    };
}


function postEditMessage(data){
    var xhr = new XMLHttpRequest();
    xhr.open("post","/chat",true);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.send(data);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
            }
            else {
                console.log('get message error');
            }
        }
    };
}

function getAction(){
    var xhr = new XMLHttpRequest();
    xhr.open("get","/chat"+"?status=new",true);
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                console.log('get message success');
                getActionFromServerWithJSON(xhr.responseText,"new");
            }
            else {
                console.log('get message error');
            }
        }
    }
}

function getActionFromServerWithJSON(jsonData){
        if (!jsonData)
            return;
        var dataFromServer =$.parseJSON(jsonData);

        var newMessages = dataFromServer.messages;
        for( i = 0; i < newMessages.length; i++){
            var messageFromServer = newMessages[i];
            var status = messageFromServer.status;
            if(status == "new"){ // NEW
                var message = newMessage(messageFromServer.date,
                                        messageFromServer.username,
                                        messageFromServer.user,
                                        messageFromServer.messageId,
                                        messageFromServer.messageText);
                addNewMessage(message);
                } else if (status == "delete") { // DELETE
                    var messageIDToDelete = messageFromServer.messageId;
                    deleteMessageByID(messageIDToDelete);
                } else if(status == "edit"){// EDIT
                    var messageIDToEdit = messageFromServer.messageId;
                    var editMessageText = messageFromServer.messageText;
                    editMessageByIDandNewText(messageIDToEdit,editMessageText);
                }
        }
    }
    function sendLogout(){
    var xhr = new XMLHttpRequest();
    xhr.open("post","/logout",true);
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == "200") {
                location.replace(xhr.responseText);
            }
            else {
                console.log('logout error');
            }
        }
    };
}
// UI
function addNewMessage(message){
    var messBlock = $(".media-list > .media:first").clone();
    messBlock.find(".message").text(message.messageText);
    messBlock.find(".NickName").text(message.username);
    messBlock.find(".Name").text(message.user);
    messBlock.find(".Time").text(message.date);
    messBlock.attr("data-messageID",[message.messageId]);
    messBlock.removeClass("hidden");
    if(currentUser == message.user){ // SameUser
    messBlock.find(".deleteMessage").click(userMessageDelete); 
    messBlock.find(".editMessage").click(userMessageEdit);
    } else { // Can't delete and edit this message
    messBlock.find(".deleteMessage").remove(); 
    messBlock.find(".editMessage").remove();
    }
    messBlock.appendTo(".media-list");
    messageList.push(message);
	scrollToBottom(chatView,false);
}
function checkForEditing (user){
    var messBlock = $(".media-list");
    for (i = 0; i < messBlock.length; i++){
        var message = messBlock[i];
        if (user == message.user){
            messBlock.find(".deleteMessage").click(userMessageDelete);
            messBlock.find(".editMessage").click(userMessageEdit);
        }
    }
}
var userMessageSended = function() { // new message sended by user
    var messageForm = $("#MessageForm");
    if (isMessageValid(messageForm.val())) {
        var prepareNewMessageForSend = newMessageSendRequest(
            currentUserName,
            currentUser,
            messageForm.val());
        messageForm.val("");
        postNewMessage(prepareNewMessageForSend);
        scrollToBottom(chatView, true);
        messageFormRefresh();
    }
};

function deleteMessageByID(messageId){
    var messBlock = $(".media-list > .media[data-messageID="+messageId+"]");
    messBlock.remove();
    deleteFromList(messageId);
}

function editMessageByIDandNewText(messageId, text){
    var messBlock = $(".media-list > .media[data-messageID="+messageId+"]");
    var textUI = messBlock.find(".message");
    textUI.text(text);
    editInList(messageId,text);
}

var userMessageDelete = function(){
    if (editMode)
        return;
    var sure = confirm("Delete Message?"); // TODO: - Nice alert view. And Animation
    if (!sure) {
        return false;
    }
    var messageToDelete = $(this).parents("li.media");
    var messageIDtoDelete = parseInt(messageToDelete.attr("data-messageID"));
    console.log('delete ' + messageIDtoDelete);
        var messageToDeleteRequestData = deleteMessageRequest(currentUserName, currentUser, messageIDtoDelete);
        postDeleteMessage(messageToDeleteRequestData);
};

var userMessageEdit = function(){ // ENTER EDIT MODE
    if (editMode)
        return;
    var messageToEdit = $(this).parents("li.media");
    messageToEditGlobal = messageToEdit.find(".message");
    UIToggleEditMode();
}; 

var messageToEditGlobal;
var editMessageEditMode = function(){ // EDIT MESSAGE 
    if (!editMode)
        return;
    var text = $("#MessageForm").val();
    if (!isMessageValid(text))
        return;
    messageToEditGlobal.text("Editing...");
    var messageIDtoEdit = parseInt(messageToEditGlobal.parents("li.media").attr("data-messageID"));
        var editRequest = editMessageRequest(currentUserName, currentUser, messageIDtoEdit, text);
        postEditMessage(editRequest);
    cancelEditMode();
};

var cancelEditMode = function(){
    if (!editMode)
        return;
    UIToggleEditMode();
};

function UIToggleEditMode(){
    if (editMode){
        editMode = false;
        messageFormRefresh();
        messageToEditGlobal.removeClass("editModeEnabled");
        $("#SendBtn").show();
        $("#EditBtn").addClass("hidden");
        $("#CancelEditBtn").addClass("hidden");
        registerKeyPress();
    } else {
        editMode = true;
        registerKeyPress(editMode);
        messageToEditGlobal.addClass("editModeEnabled");
        var textToEdit = messageToEditGlobal.text();
        $("#MessageForm").val(textToEdit);
        $("#MessageForm").focus();
        $("#SendBtn").hide();
        $("#EditBtn").removeClass("hidden");
        $("#CancelEditBtn").removeClass("hidden");
    }
}



// ----------- UI details
 var handleChatSize = function handleChatSize(){
    $('.panel-body').height($('body').height() - $('.panel-footer').height() - 40);
 };

function scrollToBottom(view,animate){
    if(animate){
        view.animate({"scrollTop": $('.panel-body')[0].scrollHeight}, "slow");
    }
    else {
        view.animate({"scrollTop": $('.panel-body')[0].scrollHeight}, 0);
    }     
}

var registerKeyPress = function(editMode){
    $(document).off("keypress");
    $(document).keypress(function(e) {
    if(e.which == 13 && !e.shiftKey) {
        if (editMode){
           editMessageEditMode();    
        } else {
           userMessageSended(); 
        }
    }
    });
};

var registerForNameCheck = function(){
    if(settingsMode){
    $(document).keyup(function(){
    var Newname = $("#NameForm").val();
    if(isMessageValid(Newname)){
        $("#NameSettingsApply").removeClass("disabled");
    } else {
        $("#NameSettingsApply").addClass("disabled");
    }
    })
    } else {
       $(document).off("keyup");
    }
};

function messageFormRefresh(){
    $("#MessageForm").val("");
    $("#MessageForm").blur();
    setTimeout(function(){
        $("#MessageForm").focus();
    }, 50);
}
var settingsHeight = "-120px";
var toggleSettings = function(){
    if (settingsMode){
        settingsMode = false;
         $(".settings").css("z-index", "-1");   $("#SettingsButtonGlyph").removeClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: '0px' });
        $('.settings').transition({ opacity: 0});
        $(".settings").css("box-shadow", "none");
        $(".settings").css("z-index", "-1");
    } else {
        settingsMode = true;
        $(".settings").css("bottom","120px");
        $(".settings").css("box-shadow", "0px 2px 3px #888888"); $("#SettingsButtonGlyph").addClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: settingsHeight
        });
        $('.settings').transition({ opacity: 100 }, function(){
            $(".settings").css("z-index", "0");
        });
    }
    registerForNameCheck();
}; 
var nameCheck = function(curUsername) {
    if (curUsername && curUsername != "") {
        return true;
    } else {
        return false;
    }
};
var applySettings = function(){
    var newName = $("#NameForm").val();
    if (isMessageValid(newName))
    currentUserName = newName;
    store();
    toggleSettings();
};
    var isMessageValid = function(messageText){
        if (!messageText ||messageText == "")
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