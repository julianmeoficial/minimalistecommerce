import React, { useEffect, useRef } from 'react';
import './PixelCanvas.css';

class Pixel {
    constructor(canvas, context, x, y, color, speed, delay) {
        this.width = canvas.width;
        this.height = canvas.height;
        this.ctx = context;
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = this.getRandomValue(0.1, 0.9) * speed;
        this.size = 0;
        this.sizeStep = Math.random() * 0.4;
        this.minSize = 0.5;
        this.maxSizeInteger = 2;
        this.maxSize = this.getRandomValue(this.minSize, this.maxSizeInteger);
        this.delay = delay;
        this.counter = 0;
        this.counterStep = Math.random() * 4 + (this.width + this.height) * 0.01;
        this.isIdle = false;
        this.isReverse = false;
        this.isShimmer = false;
    }

    getRandomValue(min, max) {
        return Math.random() * (max - min) + min;
    }

    draw() {
        const centerOffset = this.maxSizeInteger * 0.5 - this.size * 0.5;

        this.ctx.fillStyle = this.color;
        this.ctx.fillRect(
            this.x + centerOffset,
            this.y + centerOffset,
            this.size,
            this.size
        );
    }

    appear() {
        this.isIdle = false;

        if (this.counter <= this.delay) {
            this.counter += this.counterStep;
            return;
        }

        if (this.size >= this.maxSize) {
            this.isShimmer = true;
        }

        if (this.isShimmer) {
            this.shimmer();
        } else {
            this.size += this.sizeStep;
        }

        this.draw();
    }

    disappear() {
        this.isShimmer = false;
        this.counter = 0;

        if (this.size <= 0) {
            this.isIdle = true;
            return;
        } else {
            this.size -= 0.1;
        }

        this.draw();
    }

    shimmer() {
        if (this.size >= this.maxSize) {
            this.isReverse = true;
        } else if (this.size <= this.minSize) {
            this.isReverse = false;
        }

        if (this.isReverse) {
            this.size -= this.speed;
        } else {
            this.size += this.speed;
        }
    }
}

const PixelCanvas = ({ colors = "#f8fafc, #f1f5f9, #cbd5e1", gap = 5, speed = 35, noFocus = false }) => {
    const canvasRef = useRef(null);
    const parentRef = useRef(null);
    const pixelsRef = useRef([]);
    const animationRef = useRef(null);
    const timeIntervalRef = useRef(1000 / 60);
    const timePreviousRef = useRef(0);

    const colorArray = colors.split(',').map(color => color.trim());
    const gapValue = Math.max(4, Math.min(50, parseInt(gap)));
    const speedValue = Math.max(0, Math.min(100, parseInt(speed))) * 0.001;

    const createPixels = (canvas, ctx) => {
        const pixels = [];
        const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

        const getDistanceToCanvasCenter = (x, y) => {
            const dx = x - canvas.width / 2;
            const dy = y - canvas.height / 2;
            return Math.sqrt(dx * dx + dy * dy);
        };

        for (let x = 0; x < canvas.width; x += gapValue) {
            for (let y = 0; y < canvas.height; y += gapValue) {
                const color = colorArray[Math.floor(Math.random() * colorArray.length)];
                const delay = reducedMotion ? 0 : getDistanceToCanvasCenter(x, y);
                pixels.push(new Pixel(canvas, ctx, x, y, color, speedValue, delay));
            }
        }

        return pixels;
    };

    const initCanvas = () => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        const rect = parentRef.current.getBoundingClientRect();
        const width = Math.floor(rect.width);
        const height = Math.floor(rect.height);

        canvas.width = width;
        canvas.height = height;
        canvas.style.width = `${width}px`;
        canvas.style.height = `${height}px`;

        pixelsRef.current = createPixels(canvas, ctx);
    };

    const animate = (fnName) => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const ctx = canvas.getContext('2d');

        const animateFrame = () => {
            animationRef.current = requestAnimationFrame(animateFrame);

            const timeNow = performance.now();
            const timePassed = timeNow - timePreviousRef.current;

            if (timePassed < timeIntervalRef.current) return;

            timePreviousRef.current = timeNow - (timePassed % timeIntervalRef.current);

            ctx.clearRect(0, 0, canvas.width, canvas.height);

            let allIdle = true;
            for (let i = 0; i < pixelsRef.current.length; i++) {
                pixelsRef.current[i][fnName]();
                if (!pixelsRef.current[i].isIdle) {
                    allIdle = false;
                }
            }

            if (allIdle) {
                cancelAnimationFrame(animationRef.current);
            }
        };

        animationRef.current = requestAnimationFrame(animateFrame);
    };

    const handleAnimation = (name) => {
        if (animationRef.current) {
            cancelAnimationFrame(animationRef.current);
        }
        animate(name);
    };

    useEffect(() => {
        timePreviousRef.current = performance.now();

        const resizeObserver = new ResizeObserver(() => {
            initCanvas();
        });

        if (parentRef.current) {
            resizeObserver.observe(parentRef.current);

            const handleMouseEnter = () => handleAnimation('appear');
            const handleMouseLeave = () => handleAnimation('disappear');
            const handleFocusIn = (e) => {
                if (e.currentTarget.contains(e.relatedTarget)) return;
                handleAnimation('appear');
            };
            const handleFocusOut = (e) => {
                if (e.currentTarget.contains(e.relatedTarget)) return;
                handleAnimation('disappear');
            };

            parentRef.current.addEventListener('mouseenter', handleMouseEnter);
            parentRef.current.addEventListener('mouseleave', handleMouseLeave);

            if (!noFocus) {
                parentRef.current.addEventListener('focusin', handleFocusIn);
                parentRef.current.addEventListener('focusout', handleFocusOut);
            }

            // Iniciar con animación de aparecer
            setTimeout(() => {
                handleAnimation('appear');
            }, 100);

            return () => {
                resizeObserver.disconnect();
                if (parentRef.current) {
                    parentRef.current.removeEventListener('mouseenter', handleMouseEnter);
                    parentRef.current.removeEventListener('mouseleave', handleMouseLeave);

                    if (!noFocus) {
                        parentRef.current.removeEventListener('focusin', handleFocusIn);
                        parentRef.current.removeEventListener('focusout', handleFocusOut);
                    }
                }

                if (animationRef.current) {
                    cancelAnimationFrame(animationRef.current);
                }
            };
        }
    }, [noFocus]);

    return (
        <div ref={parentRef} className="pixel-canvas-container">
            <canvas ref={canvasRef} className="pixel-canvas"></canvas>
        </div>
    );
};

export default PixelCanvas;
