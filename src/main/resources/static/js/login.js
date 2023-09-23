(function () {
    let usernameCookie = getCookie('USERNAME')
    let username = document.getElementById('username')
    if (usernameCookie && !username.value) {
        username.value = usernameCookie
    }
})()

function passwordLogin() {
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
    document.getElementById('error-msg').innerHTML = ''
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
