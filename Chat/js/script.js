//ALL UI LOGIC
//ALL DAY


// ----------- VARS

var chatView;  
var messageList = [];
var globalmessageID = 0;

var editMode = false;
var settingsMode = false;

var actionID = 0;

var currentUserName;

var session_id;


var newMessage = function (userID,messageID,name,mesText){
    return {
        userID: userID,
        userName: name,
        messageID: messageID,
        mesText: mesText,
        
    };
};


var newMessageSendRequest = function (session_id,username,room_id, messageText){
    return {
        session_id: session_id,
        message: JSON.stringify( {
            username : username,
            room_id: room_id,
            text: messageText,
            status: "new",
            
            
            
        })
        
    };
};

var newGetRequest = function(session_id, action_id, username){
    return{
        session_id: session_id,
        action_id: action_id,
        username: username,    
    }
    
}

// ----------- Main Function

$( document ).ready(function(){

    handleChatSize();
    $( window ).resize(handleChatSize);
    
    chatView = $('.panel-body');
    run();
    
    
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
    
    
    alert(currentUserName);
	   


});

// ----------- LOGIC. Chat UI updates. Server Requests

function run() { // start of app. data restore.
    
    var data = restore() ;
    createAllMessages(data);
    
    startSession(currentUserName); // post func - get session
    
    if(session_id == -1){
        return; // Error
    }
    
    setInterval(function(){ // Get Requset
        alert(actionID);    
       var getRequest = newGetRequest(session_id,actionID,currentUserName)
       getAction(getRequest); // getNewActions 
       
    }, 5000)
}


function createAllMessages(data) {
    if (data != null) {
	for(var i = 0; i < data.length; i++)
		addMessageFromData(data[i]);
    };    
}

function restore() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}
    
	var item = localStorage.getItem("data");
    var messageIDopt = localStorage.getItem("messageID");
    if (messageIDopt != null){
        globalmessageID = messageIDopt;
    }
    var actionIDopt = localStorage.getItem("actionID");
    if (actionIDopt != null){
        actionID = actionIDopt;
    }
    
    var userNameopt = localStorage.getItem("userName");
    if (userNameopt != null){
        currentUserName = userNameopt;
        $("#NameForm").val(currentUserName);
    } else {
        
        currentUserName = "NoNameUser";
        
    }
    
    
    

	return item && JSON.parse(item);
}

function store(){
    if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	localStorage.setItem("data", JSON.stringify(messageList));
    localStorage.setItem("messageID", globalmessageID);
    localStorage.setItem("actionID", actionID);
    localStorage.setItem("userName", currentUserName);
}

function addMessageFromData(message){
    addNewMessage(message);
    
}

function deleteFromList(messageID){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageID == messageID){
            messageList.splice(i,1);
            store();
            continue;
        }
    }

}

function editInList(messageID,newText){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageID == messageID){
            messageList[i].mesText = newText;
            store();
            continue;
        }
    }

}



// Test Func. It woorks.
function postNewMessage(data){ 
    
    //alert(JSON.stringify(message1));
    
    $.ajax({
    method: "POST",
    url: "http://10.160.46.17:8080/ChatServer/UpdateServlet",
    data: data,    
    success: function(data){
        if(data == 0){
        alert('post message succes');
    } else {
        alert('post error in success');
    }
    },
    error: function(data){
        alert('post error - error');
    }    
    });
}


function getAction(getRequest){ 
    
    //alert(JSON.stringify(message1));
    
    $.ajax({
    method: "GET",
    url: "http://10.160.46.17:8080/ChatServer/Get",
    data: getRequest,
    dataType: JSON,
    success: function(data){
        alert("getActionSucces" + data);
        
        
        getActionFromServerWithJSON(data);
        
        
        
    },
    error: function(data){
        alert("getError");
    }    
    });
}


function startSession(username){
    
    
    $.ajax({
		url: "First",
		method: "POST",
        async : false, 
		data: {username: username},
		success: function(data) {
			alert("SessionID success" = data);
            session_id = parseInt(data);
		},
		error: function(data) {
			alert("SessionID error");
            session_id = -1;
		}
})
    
    
}

function getActionFromServerWithJSON(jsonData){
        if (jsonData.length == 0)
            return;
        
        
        
        var dataFromServer = $.parseJSON(jsonData);
        var newMessages = dataFromServer[messages];
        
        for( i = 0; i < newMessages.length; i++){
            
            var messageFromServer = newMessages[i];
            var message = newMessage(messageFromServer.user_id,
                                    messageFromServer.username,
                                    messageFromServer.message_id,
                                    messageFromServer.text);
            
            addNewMessage(message);
            
        }
        
        
        
        
    }
    
    

// UI
function addNewMessage(message){

    var messBlock = $(".media-list > .media:first").clone();
    messBlock.find(".message").text(message.mesText);
    messBlock.attr("data-messageID",[message.messageID]);
     
    messBlock.removeClass("hidden"); 
    messBlock.find(".deleteMessage").click(userMessageDelete); 
    messBlock.find(".editMessage").click(userMessageEdit);
    messBlock.appendTo(".media-list");

    messageList.push(message);
    


}; 




var userMessageSended = function(){
    
    var messageForm = $("#MessageForm");
    var message = newMessage(1,globalmessageID++,"Anton",messageForm.val());
    
    
    var prepareNewMessageForSend = newMessageSendRequest(session_id,
                                                        currentUserName,
                                                        1,
                                                        messageForm.val());
                                                      
                                                      
                        
                                  
//    if (message.mesText.length == 0){
//        return;
//    }
    
    if(!isMessageValid(message.mesText)){
        
        return;
    }
    
    messageForm.val("");
    

    //checkServerStatus();
    
   // postAction(action);
    //addNewMessage(message);
    
    postNewMessage(prepareNewMessageForSend);
    
    store();
    
    
    scrollToBottom(chatView,true);
    
    messageFormRefresh();
    
    
    
    
   
    

};


var userMessageDelete = function(evt){
    
    if (editMode)
        return;
    
    var sure = confirm("Delete Message?"); // TODO: - Nice alert view. And Animation
    if (!sure) {
        return false;
    }
    var messageToDelete = $(this).parents("li.media");
    var messageIDtoDelete = parseInt(messageToDelete.attr("data-messageID"));
    deleteFromList(messageIDtoDelete);
    messageToDelete.remove(); //TODO : - Awesome animation ASAP
};


var userMessageEdit = function(evt){
    // - TODO - EDIT
    
    if (editMode)
        return;
    
    var messageToEdit = $(this).parents("li.media");
    messageToEditGlobal = messageToEdit.find(".message");
    
    UIToggleEditMode();
    

}; 

var messageToEditGlobal;

var editMessageEditMode = function(){
    if (!editMode)
        return;
    var text = $("#MessageForm").val();
    if (!isMessageValid(text))
        return;
    
    
    messageToEditGlobal.text($("#MessageForm").val());
    var messageIDtoEdit = parseInt(messageToEditGlobal.parents("li.media").attr("data-messageID"));
    editInList(messageIDtoEdit,messageToEditGlobal.text());
    cancelEditMode();
}

var cancelEditMode = function(){
    if (!editMode)
        return;
    UIToggleEditMode();
}


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







// Does not work. Need to teach server to make callbacks.
var serverURL = "";
function checkServerStatus(){ // Rethink
    $.ajax({url: serverURL,
        type: "HEAD",
        timeout:1000,
        statusCode: {
            200: function (response) {
                alert('Working!');
            },
            400: function (response) {
                alert('Not working!');
            },
            0: function (response) {
                alert('Not working!');
            }              
        }
 });
}



// ----------- UI details you do not need to carry about. 
// Really. Don't

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
}

var registerForNameCheck = function(){
    if(settingsMode){
    $(document).keyup(function(e){
            
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
}


function messageFormRefresh(){
    $("#MessageForm").val("");
    $("#MessageForm").blur();
    setTimeout(function(){
        $("#MessageForm").focus();
    }, 50);
    
    
    
};
  
var settingsHeight = "-120px"

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
        $( "#mainRow" ).transition({ y: settingsHeight,
                                   });
        
        $('.settings').transition({ opacity: 100 }, function(){
            $(".settings").css("z-index", "0");
            
        });


    }
    
    registerForNameCheck();

}; 


var applySettings = function(){
    
    var newName = $("#NameForm").val();
    currentUserName = newName;
    store();
    
    toggleSettings();
}

    var isMessageValid = function(messageText){
        
        
        if (messageText.length == 0 )
            return false;
        
        for (var i = 0, ch; i < messageText.length; i++ ){
            ch = messageText.charCodeAt(i);
            if(ch != 32 && ch != 10){
            return true;
            }
        }
        return false;
    }








