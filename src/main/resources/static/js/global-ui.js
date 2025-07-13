
class AuthModalForm {
    #loginUsername = "";
    #loginPassword = "";
    #registerUsername = "";
    #registerEmail = "";
    #registerPassword = "";

    #e_loginUsername;
    #e_loginPassword;
    #e_registerUsername;
    #e_registerEmail;
    #e_registerPassword;

    #e_loginSection;
    #e_registerSection;

    #e_loginButton;
    #e_registerButton;

    #e_loginToggleButton;
    #e_registerToggleButton;

    #viewingLogin = true;

    static #instance;

    static init() {
        AuthModalForm.#instance = new AuthModalForm();
        AuthModalForm.#instance.#init();
    }

    // TODO init modal instance so we can close it on success
    // TODO add instant validation to register/sign in
    // TODO handle API errors
    // TODO forgot password

    #renderToggleChanges() {
        const isLogin = this.#viewingLogin;
        this.#e_loginToggleButton.classList.toggle("active", isLogin);
        this.#e_registerToggleButton.classList.toggle("active", !isLogin);
        this.#e_loginSection.classList.toggle("d-none", !isLogin);
        this.#e_registerSection.classList.toggle("d-none", isLogin);
    }

    #init() {
        this.#e_loginUsername = document.getElementById("login-username");
        this.#e_loginPassword = document.getElementById("login-password");
        this.#e_registerUsername = document.getElementById("register-username");
        this.#e_registerEmail = document.getElementById("register-email");
        this.#e_registerPassword = document.getElementById("register-password");

        this.#e_loginButton = document.getElementById("auth-modal-login-btn");
        this.#e_registerButton = document.getElementById("auth-modal-register-btn");

        this.#e_loginToggleButton = document.getElementById("auth-modal-login-toggle-btn");
        this.#e_registerToggleButton = document.getElementById("auth-modal-register-toggle-btn");

        this.#e_loginSection = document.getElementById("auth-modal-login-section");
        this.#e_registerSection = document.getElementById("auth-modal-register-section");

        this.#e_loginToggleButton.addEventListener("click", () => {
            if (this.#viewingLogin === true) return;
            this.#viewingLogin = true;
            this.#renderToggleChanges();
        });
        this.#e_registerToggleButton.addEventListener("click", () => {
            if (this.#viewingLogin === false) return;
            this.#viewingLogin = false;
            this.#renderToggleChanges();
        });
        this.#e_loginButton.addEventListener("click", () => this.#tryLogin());
        this.#e_registerButton.addEventListener("click", () => this.#tryRegister());

        this.#e_loginUsername.addEventListener("change", (e) => {
            this.#loginUsername = e.target.value;
        });

        this.#e_loginPassword.addEventListener("change", (e) => {
            this.#loginPassword = e.target.value;
        });

        this.#e_registerUsername.addEventListener("change", (e) => {
            this.#registerUsername = e.target.value;
        });

        this.#e_registerEmail.addEventListener("change", (e) => {
            this.#registerEmail = e.target.value;
        });

        this.#e_registerPassword.addEventListener("change", (e) => {
            this.#registerPassword = e.target.value;
        });

        this.#renderToggleChanges();
    }

    async #tryLogin() {
        const payload = {
            username: this.#loginUsername,
            password: this.#loginPassword
        }
        console.log("Login with", payload);
        const response = await fetch("/api/v1/account/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert("Logged in");
        } else {
            alert("Login failed");
        }
    }

    async #tryRegister() {
        const payload = {
            username: this.#registerUsername,
            email: this.#registerEmail,
            password: this.#registerPassword
        }
        console.log("Register with", payload);
        const response = await fetch("/api/v1/account/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });
        if (response.ok) {
            alert("Registered");
        } else {
            alert("Failed to register");
        }
    }
}

AuthModalForm.init();


function initMobileSidebar() {
    const bodyTag = document.querySelector("body");
    const hamburgerButton = document.getElementById("mobile-hamburger");
    const navMenu = document.getElementById("mobile-nav-menu");
    const backdrop = document.getElementById("mobile-sidebar-backdrop");

    let isOpen = false;
    const updateDisplays = () => {
        navMenu.classList.toggle("d-none", !isOpen);
        backdrop.classList.toggle("d-none", !isOpen);
        bodyTag.classList.toggle("no-overflow", isOpen);
    };

    hamburgerButton.addEventListener("click", () => {
        isOpen = !isOpen;
        updateDisplays();
    });

    backdrop.addEventListener("click", () => {
        isOpen = false;
        updateDisplays();
    });
}

function initNavbarAuthButtons() {
    const signInButton = document.getElementById("navbar-sign-in-button");
    const userButton = document.getElementById("navbar-user-button");
    signInButton.addEventListener("click", () => GlobalAuth.openLoginModal());
    userButton.addEventListener("click", () => console.log("profile"));
}



initMobileSidebar();
initNavbarAuthButtons();
