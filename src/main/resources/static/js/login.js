(function () {
    let url = document.getElementById('authorize-id').dataset.url
    if (url && location.pathname !== url) {
        history.replaceState({url: url, title: document.title}, document.title, url)
    }

    let usernameCookie = getCookie('USERNAME')
    let username = document.getElementById('username')
    if (usernameCookie && !username.value) {
        username.value = usernameCookie
    }
})()

function passwordLogin() {
    let password = document.getElementById('password')
    password.value = sha256(password.value)
    rememberMe()
}

function refreshCaptcha(element) {
    element.src = element.src.split('?')[0] + '?rnd=' + Math.random()
}

let loginByPassword = true

function changeLoginType() {
    if (loginByPassword) {
        document.getElementById('passwordForm').classList.add('d-none')
        document.getElementById('mobileForm').classList.remove('d-none')

        document.getElementById('passwordIcon').classList.remove('d-none')
        document.getElementById('mobileIcon').classList.add('d-none')
    } else {
        document.getElementById('passwordForm').classList.remove('d-none')
        document.getElementById('mobileForm').classList.add('d-none')

        document.getElementById('passwordIcon').classList.add('d-none')
        document.getElementById('mobileIcon').classList.remove('d-none')
    }
    loginByPassword = !loginByPassword
}

function rememberMe() {
    if (document.getElementById('remember-me').checked) {
        let username = document.getElementById('username').value
        if (username) {
            cookie('USERNAME', username, 7)
        }
    } else {
        removeCookie('USERNAME')
    }
}
