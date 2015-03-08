//ALL UI LOGIC
//ALL DAY


 function handleChatSize(){
        $('.panel-body').height($('body').height() - $('.panel-footer').height() - 30);


 };

 function addNewMessage(message){

    var messBlock = $(".media-list > .media:first").clone();
    messBlock.find(".message").text(message);
    messBlock.removeClass("hidden"); 
    messBlock.find(".deleteMessage").click(userMessageDelete); 
    messBlock.appendTo(".media-list");

    $("#MessageForm").blur();


}; 

var chatView;   

var userMessageSended = function(evt){
    var messageForm = $("#MessageForm");
    var message = messageForm.val()
    if (message.length == 0){
        return;
    }
    messageForm.val("");



    addNewMessage(message);

    scrollToBottom(chatView);



};


var userMessageDelete = function(evt){
    var sure = confirm("Delete Message?"); // TODO: - Nice alert view. And Animation
    if (!sure) {
        return false;
    }
    var messageToDelete = $(this).parents("li.media");
    messageToDelete.remove(); //TODO : - Awesome animation ASAP

};


var userMessageEdit = function(evt){
    // - TODO - EDIT
    alert("Some Awesome View goes Here And we Do some cool stuff by editing a message")

};    





function scrollToBottom(view){
        view.animate({"scrollTop": $('.panel-body')[0].scrollHeight}, "slow");
}    


$( document ).ready(function(){

    handleChatSize();
    $( window ).resize(handleChatSize);

    chatView = $('.panel-body');
    chatView.fadeIn(300);

    scrollToBottom(chatView);

    //NewMessageAdd
    $("#SendBtn").click(userMessageSended);
    $(document).keypress(function(e) {
    if(e.which == 13 && !e.shiftKey) {
        userMessageSended(e);
    }





});

    //DeleteMessage
    $(".deleteMessage").click(userMessageDelete);

    //EditMessage
    $(".editMessage").click(userMessageEdit);


});


