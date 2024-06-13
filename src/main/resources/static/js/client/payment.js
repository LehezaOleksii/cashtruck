document.addEventListener('DOMContentLoaded', async () => {
    const response = await fetch("/api/payment/config");
    const data = await response.json();
    const publishableKey = data.data;
    const stripe = Stripe(publishableKey)

    const priceElement = document.getElementById('payment_data-price');
    const subscriptionPlanElement = document.getElementById('payment_data-plan');
    const paymentData = {
        price: parseFloat(priceElement.textContent.trim()),
        subscriptionPlan: subscriptionPlanElement.textContent.trim(),
    };
    const {clientSecret} = await fetch("/api/payment/create-payment-intent", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(paymentData)
    }).then(r => r.json())

    const elements = stripe.elements({clientSecret})
    const paymentElement = elements.create('payment')
    paymentElement.mount('#payment-element')

    const form = document.getElementById('payment-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const {error} = stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: window.location.href.split("?")[0] + "client/complete.html"
            }
        })
        if (error) {
            const messages = document.getElementById('error-messages')
            messages.innerHTML = error.message;
        }
    })
});