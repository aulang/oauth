let sendSecurityCodeSuccess = false

const regPhone = /^1[0-9]{10}$/
const regEmail = /^\w+@[a-z0-9]+\.[a-z]+$/

function mobileLogin(element) {
    if (!sendSecurityCodeSuccess) {
        document.getElementById('error-msg').innerHTML = '请先发送验证码'
    }

    let securityCode = document.getElementById('security-code')
    if (!securityCode.reportValidity()) {
        return
    }

    let url = element.dataset.url
    let code = securityCode.value;
    let authorizeId = document.getElementById('authorize-id').value;

    let getUrl = `${url}/${authorizeId}/${code}`

    axios.get(getUrl).then((response) => {
        if (response.data) {
            document.getElementById('mobileForm').submit();
        } else {
            document.getElementById('error-msg').innerHTML = '验证码错误'
        }
    }).catch((error) => {
        if (error.response.data) {
            document.getElementById('error-msg').innerHTML = error.response.data.message
        } else {
            document.getElementById('error-msg').innerHTML = '验证码错误'
        }
    });
}

function sendSecurityCode(element) {
    let mobile = document.getElementById('mobile')
    mobile.setCustomValidity('');
    if (!mobile.reportValidity()) {
        return
    }

    let mobileValue = mobile.value
    if (!regPhone.test(mobileValue) && !regEmail.test(mobileValue)) {
        mobile.setCustomValidity('请输入手机号或邮箱');
        mobile.reportValidity()
        return
    }

    let url = element.dataset.url
    let authorizeId = document.getElementById('authorize-id').value;

    let postUrl = `${url}?authorize_id=${authorizeId}&mobile=${mobileValue}`

    element.disabled = true
    axios.post(postUrl).then(() => {
        sendSecurityCodeSuccess = true
        document.getElementById('error-msg').innerHTML = ''
        document.getElementById('error-msg').innerHTML = '验证码发送成功'

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
            document.getElementById('error-msg').innerHTML = error.response.data.message
        } else {
            document.getElementById('error-msg').innerHTML = '验证码发送失败'
        }
        element.disabled = false
    })
}