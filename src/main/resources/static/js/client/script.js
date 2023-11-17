

//  document.addEventListener("DOMContentLoaded", function () {
//    var cardHolderElement = document.querySelector(".card-holder");
//    var maxLength = parseInt(cardHolderElement.getAttribute("data-max-length"));

//    if (cardHolderElement.textContent.length > maxLength) {
//      cardHolderElement.textContent =
//        cardHolderElement.textContent.slice(0, maxLength) + "...";
//    }
//  });

     function selectBank(button) {
       var selectedBank = button.textContent;
       document.getElementById("selectedBankButton").textContent = selectedBank;
     }