const configForm = document.getElementById("configForm")
const memeConfig = document.getElementById("memeConfig")
const previewImage = document.getElementById("previewImage")

function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        const context = this;
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            func.apply(context, args);
        }, delay);
    };
}

async function handleSubmit() {
    const form = configForm
    const url = form.action;

    try {
        const formData = new FormData(form);
        const response = await fetch(url, {
            method: 'POST', body: formData
        });
        const responseBlob = await response.blob()
        previewImage.src = URL.createObjectURL(responseBlob)
    } catch (error) {
        console.error(error);
    }
}

configForm.onsubmit = async (event) => {
    event.preventDefault()
    await handleSubmit()
}

memeConfig.oninput = debounce(async () => {
    await handleSubmit()
}, 500)

