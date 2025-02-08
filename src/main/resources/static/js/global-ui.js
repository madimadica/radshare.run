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
    })
}

initMobileSidebar();
