-- Add role column to invites table
-- The role specifies what role the invited user will have in the organization upon accepting
ALTER TABLE "public"."invites" ADD COLUMN "role" text NOT NULL DEFAULT 'USER';

-- Create index for efficient email lookups (for user pending invites via notifications)
CREATE INDEX IF NOT EXISTS idx_invites_email ON public.invites(email);
