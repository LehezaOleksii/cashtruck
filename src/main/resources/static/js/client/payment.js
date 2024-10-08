document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch("/api/payment/config");
        const data = await response.json();
        const publishableKey = data.data;

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

        const priceElement = document.getElementById('payment_data-price');
        const subscriptionPlanElement = document.getElementById('payment_data-plan');
        const userIdElement = document.getElementById('user_data-id');

        const paymentData = {
            userId: userIdElement.textContent.trim(),
            subscriptionPlan: subscriptionPlanElement.textContent.trim(),
            price: priceElement.textContent.trim()
        };

        const {clientSecret} = await fetch("/api/payment/create-payment-intent", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            credentials: 'include',
            body: JSON.stringify(paymentData)
        }).then(r => r.json());

        const stripe = Stripe(publishableKey);
        const elements = stripe.elements({clientSecret});
        const paymentElement = elements.create('payment');
        paymentElement.mount('#payment-element');

        const form = document.getElementById('payment-form');
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            await stripe.confirmPayment({
                elements,
                confirmParams: {
                    return_url: window.location.origin + "/clients/dashboard"
                }
            });
        });
    } catch (error) {
        console.error('Error:', error);
    }
});
