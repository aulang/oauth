let loginByPassword = true
let sendSecurityCodeSuccess = false

function passwordLogin(e) {
    e.preventDefault()
}

function mobileLogin(e) {
    if (!sendSecurityCodeSuccess) {
        document.getElementById('error-msg').innerHTML = '请先发送验证码'
    }
    e.preventDefault()
}

function refreshCaptcha() {
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

function sendSecurityCode(e) {
    let mobile = document.getElementById('mobile')
    if (!mobile.checkValidity()) {
        mobile.reportValidity()
        return
    }

    e.target.disabled = true
    sendSecurityCodeSuccess = true

    let counter = 60
    let timer = setInterval(() => {
        counter = counter - 1

        e.target.innerHTML = `${counter}s`

        if (counter === 0) {
            e.target.innerHTML = '发送验证码'
            e.target.disabled = false
            clearInterval(timer)
        }
    }, 1000)
}
