function cookieEnabled() {
    return navigator.cookieEnabled
}

function cookie(name, value, expireDays) {
    let expireDate = new Date()
    expireDate.setDate(expireDate.getDate() + expireDays)
    document.cookie = name + '=' + encodeURI(value) + ((expireDays == null) ? '' : ';expires=' + expireDate.toGMTString())
}

function getCookie(name) {
    if (document.cookie.length > 0) {
        let start = document.cookie.indexOf(name + '=')
        if (start !== -1) {
            start = start + name.length + 1

            let end = document.cookie.indexOf(';', start)
            if (end === -1) {
                end = document.cookie.length
            }

            return decodeURI(document.cookie.substring(start, end))
        }
    }
    return ''
}

function removeCookie(name) {
    cookie(name, 'null', -1)
}