document.addEventListener('DOMContentLoaded',async()=>{
    const {publishableKey} = await fetch("/config").then(r => r.json())
    const stripe = Stripe(publishableKey)

    const {clientSecret} = await fetch("/clients/premium/payment_form/pay",{
        method : "POST",
        headers: {
            "Content-Type" : "applicetion/json"
        },
    }).then(r=>r.json())

    const elements = stripe.elements({clientSecret})
    const paymentElement = elements.create('payment')
    paymentElement.mount('#payment-element')

    const form = document.getElementById('payment-form');
    form.addEventListener('submit',async(e)=>{
        e.preventDefault();

        const {error} = stripe.confirmPayment({
            elements,
            confirmParams : {
                return_url: window.location.href.split("?")[0] + "complete.html"
            }
        })
        if(error){
            const messages = document.getElementById('error-messages')
            messages.innerHTML = error.message;
        }
    })
})