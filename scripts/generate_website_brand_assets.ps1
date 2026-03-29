param(
    [string]$OutputDir = (Join-Path $PSScriptRoot "..\\website")
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

function New-RoundedRectanglePath {
    param(
        [System.Drawing.RectangleF]$Rectangle,
        [float]$Radius
    )

    $diameter = $Radius * 2
    $path = [System.Drawing.Drawing2D.GraphicsPath]::new()

    $path.AddArc($Rectangle.X, $Rectangle.Y, $diameter, $diameter, 180, 90)
    $path.AddArc($Rectangle.Right - $diameter, $Rectangle.Y, $diameter, $diameter, 270, 90)
    $path.AddArc($Rectangle.Right - $diameter, $Rectangle.Bottom - $diameter, $diameter, $diameter, 0, 90)
    $path.AddArc($Rectangle.X, $Rectangle.Bottom - $diameter, $diameter, $diameter, 90, 90)
    $path.CloseFigure()

    return $path
}

function Use-HighQualityDrawing {
    param([System.Drawing.Graphics]$Graphics)

    $Graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $Graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $Graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $Graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
}

function Save-BrandMarkPng {
    param(
        [string]$OutputPath,
        [int]$Size
    )

    $bitmap = [System.Drawing.Bitmap]::new($Size, $Size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)

    try {
        Use-HighQualityDrawing -Graphics $graphics
        $graphics.Clear([System.Drawing.Color]::Transparent)

        $cardInset = [Math]::Round($Size * 0.07)
        $cardRect = [System.Drawing.RectangleF]::new(
            [float]$cardInset,
            [float]$cardInset,
            [float]($Size - ($cardInset * 2)),
            [float]($Size - ($cardInset * 2)))
        $cardRadius = [Math]::Round($Size * 0.22)

        $cardPath = New-RoundedRectanglePath -Rectangle $cardRect -Radius $cardRadius
        try {
            $brush = [System.Drawing.Drawing2D.LinearGradientBrush]::new(
                [System.Drawing.PointF]::new($cardRect.Left, $cardRect.Top),
                [System.Drawing.PointF]::new($cardRect.Right, $cardRect.Bottom),
                [System.Drawing.ColorTranslator]::FromHtml("#ff8a4d"),
                [System.Drawing.ColorTranslator]::FromHtml("#ffb472"))
            try {
                $graphics.FillPath($brush, $cardPath)
            }
            finally {
                $brush.Dispose()
            }
        }
        finally {
            $cardPath.Dispose()
        }

        $fontSize = [Math]::Round($Size * 0.46)
        $font = [System.Drawing.Font]::new("Segoe UI", $fontSize, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
        $format = [System.Drawing.StringFormat]::new()
        $format.Alignment = [System.Drawing.StringAlignment]::Center
        $format.LineAlignment = [System.Drawing.StringAlignment]::Center

        try {
            $graphics.DrawString("W", $font, [System.Drawing.Brushes]::White, $cardRect, $format)
        }
        finally {
            $format.Dispose()
            $font.Dispose()
        }

        $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Save-FaviconIco {
    param(
        [string]$IconPath,
        [string]$PngPath
    )

    $pngBytes = [System.IO.File]::ReadAllBytes($PngPath)
    $stream = [System.IO.File]::Open($IconPath, [System.IO.FileMode]::Create, [System.IO.FileAccess]::Write)
    $writer = [System.IO.BinaryWriter]::new($stream)

    try {
        $writer.Write([UInt16]0)
        $writer.Write([UInt16]1)
        $writer.Write([UInt16]1)
        $writer.Write([byte]64)
        $writer.Write([byte]64)
        $writer.Write([byte]0)
        $writer.Write([byte]0)
        $writer.Write([UInt16]1)
        $writer.Write([UInt16]32)
        $writer.Write([UInt32]$pngBytes.Length)
        $writer.Write([UInt32]22)
        $writer.Write($pngBytes)
    }
    finally {
        $writer.Dispose()
        $stream.Dispose()
    }
}

function Save-SocialPreview {
    param([string]$OutputPath)

    $width = 1200
    $height = 630
    $bitmap = [System.Drawing.Bitmap]::new($width, $height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)

    try {
        Use-HighQualityDrawing -Graphics $graphics

        $backgroundBrush = [System.Drawing.Drawing2D.LinearGradientBrush]::new(
            [System.Drawing.PointF]::new(0, 0),
            [System.Drawing.PointF]::new($width, $height),
            [System.Drawing.ColorTranslator]::FromHtml("#0d1827"),
            [System.Drawing.ColorTranslator]::FromHtml("#162b43"))

        try {
            $graphics.FillRectangle($backgroundBrush, 0, 0, $width, $height)
        }
        finally {
            $backgroundBrush.Dispose()
        }

        $mintBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(42, 49, 201, 177))
        $blueBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(46, 122, 167, 255))
        $goldBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(36, 244, 198, 109))
        try {
            $graphics.FillEllipse($mintBrush, -140, -100, 420, 340)
            $graphics.FillEllipse($blueBrush, 780, -120, 420, 300)
            $graphics.FillEllipse($goldBrush, 870, 360, 300, 220)
        }
        finally {
            $mintBrush.Dispose()
            $blueBrush.Dispose()
            $goldBrush.Dispose()
        }

        $panelRect = [System.Drawing.RectangleF]::new(72, 72, 1056, 486)
        $panelPath = New-RoundedRectanglePath -Rectangle $panelRect -Radius 38
        try {
            $panelBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(28, 255, 255, 255))
            $panelPen = [System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(32, 255, 255, 255), 2)
            try {
                $graphics.FillPath($panelBrush, $panelPath)
                $graphics.DrawPath($panelPen, $panelPath)
            }
            finally {
                $panelBrush.Dispose()
                $panelPen.Dispose()
            }
        }
        finally {
            $panelPath.Dispose()
        }

        $brandRect = [System.Drawing.RectangleF]::new(120, 132, 170, 170)
        $brandPath = New-RoundedRectanglePath -Rectangle $brandRect -Radius 42
        try {
            $brandBrush = [System.Drawing.Drawing2D.LinearGradientBrush]::new(
                [System.Drawing.PointF]::new($brandRect.Left, $brandRect.Top),
                [System.Drawing.PointF]::new($brandRect.Right, $brandRect.Bottom),
                [System.Drawing.ColorTranslator]::FromHtml("#ff8a4d"),
                [System.Drawing.ColorTranslator]::FromHtml("#ffb472"))
            try {
                $graphics.FillPath($brandBrush, $brandPath)
            }
            finally {
                $brandBrush.Dispose()
            }
        }
        finally {
            $brandPath.Dispose()
        }

        $logoFont = [System.Drawing.Font]::new("Segoe UI", 92, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
        $titleFont = [System.Drawing.Font]::new("Segoe UI", 66, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
        $subtitleFont = [System.Drawing.Font]::new("Segoe UI", 30, [System.Drawing.FontStyle]::Regular, [System.Drawing.GraphicsUnit]::Pixel)
        $pillFont = [System.Drawing.Font]::new("Segoe UI", 24, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
        $format = [System.Drawing.StringFormat]::new()
        $format.Alignment = [System.Drawing.StringAlignment]::Center
        $format.LineAlignment = [System.Drawing.StringAlignment]::Center

        try {
            $graphics.DrawString("W", $logoFont, [System.Drawing.Brushes]::White, $brandRect, $format)
            $graphics.DrawString("Whisper Live Subtitles", $titleFont, [System.Drawing.Brushes]::White, 332, 148)

            $subtitleBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(224, 240, 246, 255))
            try {
                $graphics.DrawString(
                    "Live captions for videos, meetings, classes, and everyday listening.",
                    $subtitleFont,
                    $subtitleBrush,
                    [System.Drawing.RectangleF]::new(336, 248, 680, 120))
            }
            finally {
                $subtitleBrush.Dispose()
            }

            $pillBrushes = @(
                [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(48, 49, 201, 177)),
                [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(48, 122, 167, 255)),
                [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(48, 244, 198, 109))
            )
            $pillTexts = @("Windows app", "Transcript + Translate", "Free download")
            $pillRects = @(
                [System.Drawing.RectangleF]::new(336, 382, 180, 56),
                [System.Drawing.RectangleF]::new(536, 382, 280, 56),
                [System.Drawing.RectangleF]::new(836, 382, 180, 56)
            )

            for ($i = 0; $i -lt $pillRects.Count; $i++) {
                $pillPath = New-RoundedRectanglePath -Rectangle $pillRects[$i] -Radius 28
                try {
                    $graphics.FillPath($pillBrushes[$i], $pillPath)
                }
                finally {
                    $pillPath.Dispose()
                }
                $graphics.DrawString($pillTexts[$i], $pillFont, [System.Drawing.Brushes]::White, $pillRects[$i], $format)
            }

            foreach ($brush in $pillBrushes) {
                $brush.Dispose()
            }
        }
        finally {
            $format.Dispose()
            $logoFont.Dispose()
            $titleFont.Dispose()
            $subtitleFont.Dispose()
            $pillFont.Dispose()
        }

        $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

$resolvedOutputDir = (Resolve-Path $OutputDir).Path
$faviconSourcePath = Join-Path $resolvedOutputDir "favicon-source-64.png"

Save-BrandMarkPng -OutputPath (Join-Path $resolvedOutputDir "android-chrome-512x512.png") -Size 512
Save-BrandMarkPng -OutputPath (Join-Path $resolvedOutputDir "android-chrome-192x192.png") -Size 192
Save-BrandMarkPng -OutputPath (Join-Path $resolvedOutputDir "apple-touch-icon.png") -Size 180
Save-BrandMarkPng -OutputPath (Join-Path $resolvedOutputDir "favicon-32x32.png") -Size 32
Save-BrandMarkPng -OutputPath $faviconSourcePath -Size 64
Save-FaviconIco -IconPath (Join-Path $resolvedOutputDir "favicon.ico") -PngPath $faviconSourcePath
Save-SocialPreview -OutputPath (Join-Path $resolvedOutputDir "social-preview.png")
Remove-Item $faviconSourcePath -ErrorAction SilentlyContinue
