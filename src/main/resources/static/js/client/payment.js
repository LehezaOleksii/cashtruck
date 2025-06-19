document.addEventListener('DOMContentLoaded', async () => {
    try {
        // 1. Отримуємо публічний ключ Stripe
        const configRes = await fetch("/api/payment/config");
        const { data: publishableKey } = await configRes.json();

        // 2. CSRF-токен для безпечних PUT-запитів
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

        // 3. Читаємо дані зі сторінки
        const price = document.getElementById('payment_data-price').textContent.trim();
        const subscriptionPlan = document.getElementById('payment_data-plan').textContent.trim();
        const userId = document.getElementById('user_data-id').textContent.trim();

        // 4. Створюємо PaymentIntent
        const createRes = await fetch("/api/payment/create-payment-intent", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            credentials: 'include',
            body: JSON.stringify({ userId, subscriptionPlan, price })
        });
        const { clientSecret } = await createRes.json();

        // 5. Ініціалізуємо Stripe Elements
        const stripe = Stripe(publishableKey);
        const elements = stripe.elements({ clientSecret });
        const paymentElement = elements.create('payment');
        paymentElement.mount('#payment-element');

        // 6. Обробник сабміту форми
        const form = document.getElementById('payment-form');
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            try {
                const updateRes = await fetch('/api/payment/update-user-status', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    credentials: 'include',
                    body: JSON.stringify({ userId, price, subscriptionPlan })
                });

                if (!updateRes.ok) {
                    throw new Error(`HTTP ${updateRes.status}`);
                }

                const updatedStatus = await updateRes.json();
                console.log('User status updated:', updatedStatus);
                window.location.href = window.location.origin + "/clients/dashboard";

            } catch (updError) {
                console.error('Failed to update user status:', updError);
            }            // 6.1 Підтверджуємо платіж
            const { error } = await stripe.confirmPayment({
                elements,
                confirmParams: {
                    return_url: window.location.origin + "/clients/dashboard"
                }
            });

        });

    } catch (error) {
        console.error('General error in payment flow:', error);
    }
});
