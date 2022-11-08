let loginByPassword = true
let sendSecurityCodeSuccess = false

function init() {
    let usernameCookie = getCookie('USERNAME')
    let username = document.getElementById('username')
    if (usernameCookie && !username.value) {
        username.value = usernameCookie
    }
}

init()

function passwordLogin() {
    let password = document.getElementById('password')
    password.value = sha256(password.value)
    rememberMe()
}

function mobileLogin(e) {
    if (!sendSecurityCodeSuccess) {
        document.getElementById('error-msg').innerHTML = '请先发送验证码'
    }

    let securityCode = document.getElementById('security-code')
    if (!securityCode.checkValidity()) {
        securityCode.reportValidity()
        return
    }

    // TODO 获取URL
    let url = element.dataset.url
    let authorizeId = document.getElementById('authorize-id').value;

    let getUrl = `${url}/${authorizeId}/${code}`

    e.preventDefault()
}

function refreshCaptcha(element) {
    element.src = element.src.split('?')[0] + '?rnd=' + Math.random()
}

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

function sendSecurityCode(element) {
    let mobile = document.getElementById('mobile')
    if (!mobile.checkValidity()) {
        mobile.reportValidity()
        return
    }

    let mobileValue = mobile.value
    let url = element.dataset.url
    let authorizeId = document.getElementById('authorize-id').value;

    let postUrl = `${url}?authorize_id=${authorizeId}&mobile=${mobileValue}`

    element.disabled = true
    axios.post(postUrl).then(() => {
        sendSecurityCodeSuccess = true
        document.getElementById('error-msg').innerHTML = ''

        let counter = 60
        let timer = setInterval(() => {
            counter = counter - 1

            element.innerHTML = `${counter}s`

            if (counter === 0) {
                element.innerHTML = '发送验证码'
                element.disabled = false
                clearInterval(timer)
            }
        }, 1000)
    }).catch((error) => {
        if (error.response.data) {
            document.getElementById('error-msg').innerHTML = error.response.data
        } else {
            document.getElementById('error-msg').innerHTML = '验证码发送失败'
        }
        element.disabled = false
    })
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