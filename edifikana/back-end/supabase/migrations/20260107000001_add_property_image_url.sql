-- Add image_url column to properties table
-- This allows properties to have either default icons or custom image URLs
-- Format: "drawable:ICON_NAME" for default icons or "https://..." for custom URLs
ALTER TABLE "public"."properties"
ADD COLUMN "image_url" text;

COMMENT ON COLUMN "public"."properties"."image_url" IS
'Property image URL. Format: "drawable:CASA" | "drawable:QUINTA" | "drawable:L_DEPA" | "drawable:M_DEPA" | "drawable:S_DEPA" for default icons, or "https://..." for custom URLs. NULL indicates no image.';
