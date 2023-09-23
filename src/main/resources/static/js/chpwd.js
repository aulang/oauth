function changePassword(e) {
    let password = document.getElementById('password')
    let repassword = document.getElementById('repassword')

    if (password.value !== repassword.value) {
        document.getElementById('error-msg').innerHTML = '两次密码不一致'
        e.preventDefault()
        return
    }
}
