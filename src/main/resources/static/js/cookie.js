function cookieDisabled() {
    return !navigator.cookieEnabled
}

function cookie(name, value, expireDays) {
    if (cookieDisabled()) {
        return
    }

    let expireDate = new Date()
    expireDate.setDate(expireDate.getDate() + expireDays)
    document.cookie = name + '=' + encodeURI(value) + ((expireDays == null) ? '' : ';expires=' + expireDate.toGMTString())
}

function getCookie(name) {
    if (cookieDisabled()) {
        return ''
    }

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
    if (cookieDisabled()) {
        return
    }

    cookie(name, 'null', -1)
}