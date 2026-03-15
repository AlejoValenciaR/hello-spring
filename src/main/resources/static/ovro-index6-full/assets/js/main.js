; (function ($) {

    $(document).ready(function () {

        //========== SIDEBAR/SEARCH AREA ============= //
        $(".hamburger_menu").on("click", function (e) {
            e.preventDefault();
            $(".slide-bar").toggleClass("show");
            $("body").addClass("on-side");
            $('.body-overlay').addClass('active');
            $(this).addClass('active');
        });
        $(".close-mobile-menu > a").on("click", function (e) {
            e.preventDefault();
            $(".slide-bar").removeClass("show");
            $("body").removeClass("on-side");
            $('.body-overlay').removeClass('active');
            $('.hamburger_menu').removeClass('active');
        });
        //========== SIDEBAR/SEARCH AREA ============= //

        //========== PAGE PROGRESS STARTS ============= // 
        var progressPath = document.querySelector(".progress-wrap path");
        var pathLength = progressPath.getTotalLength();
        progressPath.style.transition = progressPath.style.WebkitTransition =
            "none";
        progressPath.style.strokeDasharray = pathLength + " " + pathLength;
        progressPath.style.strokeDashoffset = pathLength;
        progressPath.getBoundingClientRect();
        progressPath.style.transition = progressPath.style.WebkitTransition =
            "stroke-dashoffset 10ms linear";
        var updateProgress = function () {
            var scroll = $(window).scrollTop();
            var height = $(document).height() - $(window).height();
            var progress = pathLength - (scroll * pathLength) / height;
            progressPath.style.strokeDashoffset = progress;
        };
        updateProgress();
        $(window).scroll(updateProgress);
        var offset = 50;
        var duration = 550;
        jQuery(window).on("scroll", function () {
            if (jQuery(this).scrollTop() > offset) {
                jQuery(".progress-wrap").addClass("active-progress");
            } else {
                jQuery(".progress-wrap").removeClass("active-progress");
            }
        });
        jQuery(".progress-wrap").on("click", function (event) {
            event.preventDefault();
            jQuery("html, body").animate({ scrollTop: 0 }, duration);
            return false;
        });
        //========== PAGE PROGRESS STARTS ============= // 

        //========== VIDEO POPUP STARTS ============= //
        if ($(".popup-youtube").length > 0) {
            $(".popup-youtube").magnificPopup({
                type: "iframe",
            });
        }
        //========== VIDEO POPUP ENDS ============= //
        AOS.init;
        AOS.init({ disable: 'mobile' });

        //========== NICE SELECT ============= //
        $('select').niceSelect();

    });
    //========== COUNTER UP============= //
    const ucounter = $('.counter');
    if (ucounter.length > 0) {
        ucounter.countUp();
    };

    //========== PRELOADER ============= //
    $(window).on("load", function (event) {
        setTimeout(function () {
            $("#preloader").fadeToggle();
        }, 200);

    });

    //========== POPUP AREA ============= //
    $(".click-here").on('click', function () {
        $(".custom-model-main").addClass('model-open');
    });
    $(".close-btn, .bg-overlay").click(function () {
        $(".custom-model-main").removeClass('model-open');
    });

})(jQuery);

//========== GSAP AREA ============= //
if ($('.reveal').length) { gsap.registerPlugin(ScrollTrigger); let revealContainers = document.querySelectorAll(".reveal"); revealContainers.forEach((container) => { let image = container.querySelector("img"); let tl = gsap.timeline({ scrollTrigger: { trigger: container, toggleActions: "play none none none" } }); tl.set(container, { autoAlpha: 1 }); tl.from(container, 1.5, { xPercent: -100, ease: Power2.out }); tl.from(image, 1.5, { xPercent: 100, scale: 1.3, delay: -1.5, ease: Power2.out }); }); }

// Theme toggle functionality
const toggleButton = document.getElementById('theme-toggle');
if (localStorage.getItem('theme') === 'dark') {
    document.body.classList.add('light-mode');
    toggleButton.checked = true;
}
toggleButton.addEventListener('change', () => {
    document.body.classList.toggle('light-mode');

    if (document.body.classList.contains('light-mode')) {
        localStorage.setItem('theme', 'light');
    } else {
        localStorage.setItem('theme', 'dark-mode');
    }
});

function fitHeroTitleLine(element, maxWidth, minFontPx, allowScaleFallback) {
    if (!element) {
        return;
    }

    element.style.fontSize = '';
    element.style.transform = '';
    element.style.transformOrigin = '';

    let currentFontSize = parseFloat(window.getComputedStyle(element).fontSize);
    while (element.scrollWidth > maxWidth && currentFontSize > minFontPx) {
        currentFontSize -= 0.5;
        element.style.fontSize = `${currentFontSize}px`;
    }

    if (allowScaleFallback && element.scrollWidth > maxWidth) {
        const scaleX = Math.max(maxWidth / element.scrollWidth, 0.82);
        element.style.transform = `scaleX(${scaleX})`;
        element.style.transformOrigin = 'right center';
    }
}

function fitHeroTitleRow(heading, mainLine, accentLine, minMainFontPx, minAccentFontPx) {
    if (!heading || !mainLine || !accentLine) {
        return;
    }

    let mainFontSize = parseFloat(window.getComputedStyle(mainLine).fontSize);
    let accentFontSize = parseFloat(window.getComputedStyle(accentLine).fontSize);

    while (heading.scrollWidth > heading.clientWidth && accentFontSize > minAccentFontPx) {
        accentFontSize -= 0.5;
        accentLine.style.fontSize = `${accentFontSize}px`;
    }

    while (heading.scrollWidth > heading.clientWidth && mainFontSize > minMainFontPx) {
        mainFontSize -= 0.5;
        mainLine.style.fontSize = `${mainFontSize}px`;
    }

    if (heading.scrollWidth > heading.clientWidth) {
        const headingStyles = window.getComputedStyle(heading);
        const gap = parseFloat(headingStyles.columnGap || headingStyles.gap || '0');
        const availableAccentWidth = Math.max(
            heading.clientWidth - mainLine.getBoundingClientRect().width - gap,
            140
        );
        fitHeroTitleLine(accentLine, availableAccentWidth, minAccentFontPx, true);
    }
}

function updateHeroTitleFit() {
    const heading = document.querySelector('.cv-hero-title-secondary');
    const mainLine = heading?.querySelector('.cv-hero-title-secondary-main');
    const accentLine = heading?.querySelector('.cv-hero-title-secondary-accent');

    if (!heading || !mainLine || !accentLine) {
        return;
    }

    mainLine.style.fontSize = '';
    mainLine.style.transform = '';
    mainLine.style.transformOrigin = '';
    accentLine.style.fontSize = '';
    accentLine.style.transform = '';
    accentLine.style.transformOrigin = '';

    if (window.innerWidth > 1199) {
        fitHeroTitleRow(heading, mainLine, accentLine, 34, 28);
        return;
    }

    const gutterAllowance = window.innerWidth <= 767 ? 28 : 20;
    const maxWidth = Math.max(heading.clientWidth - gutterAllowance, 140);

    fitHeroTitleLine(mainLine, maxWidth, window.innerWidth <= 575 ? 16 : 18, false);
    fitHeroTitleLine(accentLine, maxWidth, window.innerWidth <= 575 ? 11 : 12, true);
}

function fitExperienceRoleTitle(roleElement) {
    if (!roleElement) {
        return;
    }

    roleElement.style.fontSize = '';
    roleElement.style.transform = '';
    roleElement.style.transformOrigin = '';

    const maxWidth = roleElement.clientWidth;
    if (!maxWidth) {
        return;
    }

    const getMaxHeight = () => {
        const styles = window.getComputedStyle(roleElement);
        const lineHeight = parseFloat(styles.lineHeight) || parseFloat(styles.fontSize) * 1.16;
        return lineHeight * 2 + 2;
    };

    let currentFontSize = parseFloat(window.getComputedStyle(roleElement).fontSize);
    let maxHeight = getMaxHeight();
    while ((roleElement.scrollWidth > maxWidth || roleElement.scrollHeight > maxHeight) && currentFontSize > 10) {
        currentFontSize -= 0.5;
        roleElement.style.fontSize = `${currentFontSize}px`;
        maxHeight = getMaxHeight();
    }

    if (roleElement.scrollWidth > maxWidth || roleElement.scrollHeight > maxHeight) {
        const scaleX = Math.max(maxWidth / roleElement.scrollWidth, 0.82);
        roleElement.style.transform = `scaleX(${scaleX})`;
        roleElement.style.transformOrigin = 'left center';
    }
}

function updateExperienceRoleFit() {
    document.querySelectorAll('.cv-featured-role').forEach(fitExperienceRoleTitle);
}

const scheduleHeroTitleFit = () => {
    window.requestAnimationFrame(updateHeroTitleFit);
};

scheduleHeroTitleFit();
window.addEventListener('resize', scheduleHeroTitleFit, { passive: true });

const scheduleExperienceRoleFit = () => {
    window.requestAnimationFrame(updateExperienceRoleFit);
};

scheduleExperienceRoleFit();
window.addEventListener('resize', scheduleExperienceRoleFit, { passive: true });

if (document.fonts && document.fonts.ready) {
    document.fonts.ready.then(() => {
        scheduleHeroTitleFit();
        scheduleExperienceRoleFit();
    });
}

if (window.ResizeObserver) {
    const heroResizeTarget = document.querySelector('.main-hero-area5 .hero-heading-area');
    if (heroResizeTarget) {
        const heroTitleObserver = new ResizeObserver(scheduleHeroTitleFit);
        heroTitleObserver.observe(heroResizeTarget);
    }

    document.querySelectorAll('.cv-experience-top').forEach((experienceTop) => {
        const experienceObserver = new ResizeObserver(scheduleExperienceRoleFit);
        experienceObserver.observe(experienceTop);
    });
}

// UPDATE: I was able to get this working again... Enjoy!
var cursor = document.querySelector('.procus-cursor');
var cursorinner = document.querySelector('.procus-cursor2');
var a = document.querySelectorAll('a');

document.addEventListener('mousemove', function (e) {
    var x = e.clientX;
    var y = e.clientY;
    cursor.style.transform = `translate3d(calc(${e.clientX}px - 50%), calc(${e.clientY}px - 50%), 0)`
});

document.addEventListener('mousemove', function (e) {
    var x = e.clientX;
    var y = e.clientY;
    cursorinner.style.left = x + 'px';
    cursorinner.style.top = y + 'px';
});

document.addEventListener('mousedown', function () {
    cursor.classList.add('click');
    cursorinner.classList.add('cursorinnerhover')
});

document.addEventListener('mouseup', function () {
    cursor.classList.remove('click')
    cursorinner.classList.remove('cursorinnerhover')
});

a.forEach(item => {
    item.addEventListener('mouseover', () => {
        cursor.classList.add('hover');
    });
    item.addEventListener('mouseleave', () => {
        cursor.classList.remove('hover');
    });
})
