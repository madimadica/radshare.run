/**
 * Extract the content value of a `<meta>` head tag
 * @param {string} name
 * @return {string}
 */
function getMetadata(name) {
    return document.querySelector(`metadata[name="${name}"]`).getAttribute("content");
}

document.addEventListener('DOMContentLoaded', function() {
    const token = getMetadata("_csrf");
    const header = getMetadata("_csrf_header");
    const originalFetch = window.fetch;
    window.fetch = function(url, options = {}) {
        options.headers = options.headers || {};
        options.headers[header] = token;
        return originalFetch(url, options);
    }
});

function parseDom(str, depth=0) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(str, "text/html");
    let node = doc.body;
    while (depth > 0) {
        depth--;
        node = node.children[0];
    }
    return node;
}

/**
 * @param {string} str HTML to parse into an element
 * @return {Element} parsed Element node
 */
function parseElement(str) {
    const temp = document.createElement("div");
    temp.innerHTML = str;
    return temp.firstElementChild;
}

/**
 * @return {void}
 * @param {HTMLElement} element
 */
function showModal(element) {
    const modal = new bootstrap.Modal(element);
    modal.show();
}

/**
 * @param o1
 * @param o2
 * @return {boolean} if both elements are equal, and all children are recursively equal
 */
function deepEquals(o1, o2) {
    if (o1 === o2) {
        return true;
    }

    if (o1 === null || o2 == null || typeof o1 !== "object" || typeof o2 !== "object") {
        return false;
    }

    const keys1 = Object.keys(o1);
    const keys2 = Object.keys(o2);

    if (keys1.length !== keys2.length) {
        return false;
    }

    for (const k of keys1) {
        if (!keys2.includes(k) || !deepEquals(o1[k], o2[k])) {
            return false;
        }
    }
    return true;
}

/**
 * @param {string | null} className
 * @param {string | null} textContent
 * @return {HTMLDivElement}
 */
function $div(className = null, textContent = null) {
    const element = document.createElement("div");
    if (className) {
        element.className = className;
    }
    if (textContent !== null) {
        element.textContent = textContent;
    }
    return element;
}

/**
 * @param {string | null} className
 * @param {string | null} textContent
 * @return {HTMLSpanElement}
 */
function $span(className = null, textContent = null) {
    const element = document.createElement("span");
    if (className) {
        element.className = className;
    }
    if (textContent !== null) {
        element.textContent = textContent;
    }
    return element;
}

/**
 * @param {string} iconName
 * @return {HTMLElement}
 */
function $bsIcon(iconName) {
    const icon = document.createElement("i");
    icon.className = `bi bi-${iconName}`;
    return icon;
}

/**
 * @param {string} text
 * @param {string} className
 * @return {HTMLButtonElement}
 */
function $button(text="", className="btn btn-primary") {
    const button = document.createElement("button");
    button.className = className;
    button.textContent = text;
    return button;
}

/**
 * @param {string} className
 * @return {HTMLElement}
 */
function $i(className) {
    const i = document.createElement("i");
    i.className = className;
    return i;
}

/**
 * @param {string | Element} arg
 * @param {string | null} className optional className property
 * @return {HTMLTableCellElement}
 */
function $td(arg, className=null) {
    const td = document.createElement("td");
    if (typeof arg === "string") {
        td.textContent = arg;
    } else if (arg instanceof Element) {
        td.appendChild(arg);
    }
    if (className) {
        td.className = className;
    }
    return td;
}

/**
 * @param {string} href
 * @param {string} textContent
 * @param {string} className
 * @param {boolean} newTab
 * @return {HTMLAnchorElement}
 */
function $a(href, textContent, className="", newTab=true) {
    const a = document.createElement("a");
    a.setAttribute("href", href);
    a.textContent = textContent;
    a.className = className;
    if (newTab) {
        a.setAttribute("target", "_blank");
    }
    a.setAttribute("rel", "noopener noreferrer");
    return a;
}

function $svg(htmlStr) {
    return parseElement(htmlStr);
}

function ltrim(s, chars=" ") {
    let start = 0;
    const len = s.length;
    while (start < len && chars.includes(s[start])) {
        start++;
    }
    return s.substring(start);
}

function rtrim(s, chars=" ") {
    let end = s.length - 1;
    while (end >= 0 && chars.includes(s[end])) {
        end--;
    }
    return s.substring(0, end + 1);
}



/**
 * Interpret the datetime string as UTC, even if it doesnt end in "Z"
 * @param {string} utcStr
 * @return {Date}
 */
function forceUtcDate(utcStr) {
    return new Date(rtrim(utcStr, "Z") + "Z");
}


/**
 * @typedef {Object} CreateDomArg
 * @property {HTMLElement} root
 * @property {CreateDomArg|HTMLElement|SVGElement|string|null} child
 * @property {Array<CreateDomArg|HTMLElement|SVGElement|string|null>} children
 */

/**
 *
 * @param {CreateDomArg|HTMLElement|SVGElement|string} node
 * @return {HTMLElement|SVGElement|Text}
 */
function createDOM(node) {
    if (node === null) {
        throw new Error("'node' cannot be null");
    } else if (node === undefined) {
        throw new Error("'node' cannot be undefined");
    }
    if ((node instanceof HTMLElement) || (node instanceof SVGElement)) {
        return node;
    }
    if (typeof node === "string") {
        return document.createTextNode(node);
    }
    if ("nodeType" in node && node.nodeType === Node.TEXT_NODE) {
        return node;
    }
    if (!(typeof node === "object" && node.constructor === Object)) {
        throw new Error("Illegal argument, expected 'node' to be a JSON object or Element");
    }
    const root = node.root;
    if (!root) {
        throw new Error("Illegal argument, expected 'node' object to have 'root' property");
    }
    if (!(root instanceof HTMLElement)) {
        throw new Error("Illegal argument, expected 'root' to be of type HTMLElement");
    }
    const hasChild = "child" in node;
    const hasChildren = "children" in node;
    if (!hasChild && !hasChildren) {
        throw new Error("Illegal argument, expected 'node' to have 'child' or 'children' property");
    }
    if (hasChild && hasChildren) {
        throw new Error("Illegal argument, 'node' has both 'child' or 'children' properties, expected only 1");
    }
    if (hasChild) {
        const child = createDOM(node.child);
        if (child) {
            root.appendChild(child);
        }
    } else {
        if (!Array.isArray(node.children)) {
            throw new Error("Illegal argument, expected 'children' to be of type Array");
        }
        for (const childNode of node.children) {
            const child = createDOM(childNode);
            if (child) {
                root.appendChild(child);
            }
        }
    }``
    return root;
}

/**
 * @param {string} specifier
 * @param {object | null} attributes
 * @return {HTMLElement}
 */
function $element(specifier, attributes=null) {
    let id = null;
    const classes = [];
    const len = specifier.length;
    let endOfTag = -1;
    for (let i = 0; i < len; ++i) {
        let ch = specifier.charAt(i);
        if (ch === "." || ch === "#") {
            endOfTag = i;
            break;
        }
    }
    let tagName = specifier;
    if (endOfTag !== -1) {
        tagName = specifier.substring(0, endOfTag);
        let startPos = endOfTag;
        const values = [];
        for (let i = endOfTag; i < len; ++i) {
            const ch = specifier.charAt(i);
            if (ch  === "." || ch === "#") {
                values.push(specifier.substring(startPos, i));
                startPos = i;
            }
        }
        values.push(specifier.substring(startPos, len));
        let hasId = false;
        for (const v of values) {
            if (v.length <= 1) {
                throw new Error("Illegal argument, expected class or ID value after '.' or '#'");
            }
            if (v.charAt(0) === ".") {
                classes.push(v.substring(1));
            } else {
                if (hasId) {
                    throw new Error("Illegal argument, cannot have more than one ID specified");
                }
                id = v.substring(1);
                hasId = true;
            }
        }
    }
    const element = document.createElement(tagName);
    if (id) {
        element.id = id;
    }
    element.className = classes.join(" ");

    if (attributes) {
        for (const key in attributes) {
            const value = attributes[key];
            if (key === "className") {
                element.className = value;
            } else if (key === "textContent") {
                element.textContent = value;
            } else {
                element.setAttribute(key, value);
            }
        }
    }
    return element;
}
