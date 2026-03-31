import { FormEvent, useEffect, useState } from "react";

type ReviewEditorProps = {
  initialText?: string;
  initialContainsSpoilers?: boolean;
  onSave: (text: string, containsSpoilers: boolean) => Promise<void>;
  onCancel?: () => void;
  submitLabel: string;
};

export function ReviewEditor({
  initialText = "",
  initialContainsSpoilers = false,
  onSave,
  onCancel,
  submitLabel
}: ReviewEditorProps) {
  const [text, setText] = useState(initialText);
  const [containsSpoilers, setContainsSpoilers] = useState(initialContainsSpoilers);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    setText(initialText);
    setContainsSpoilers(initialContainsSpoilers);
  }, [initialText, initialContainsSpoilers]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setIsSaving(true);
    try {
      await onSave(text, containsSpoilers);
      if (!onCancel) {
        setText("");
        setContainsSpoilers(false);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to save review");
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="review-editor">
      <textarea
        value={text}
        onChange={(event) => setText(event.target.value)}
        minLength={5}
        maxLength={1000}
        placeholder="Share your thoughts about this movie"
        required
      />
      <label className="inline-check">
        <input
          type="checkbox"
          checked={containsSpoilers}
          onChange={(event) => setContainsSpoilers(event.target.checked)}
        />
        Contains spoilers
      </label>
      <div className="review-actions">
        <button type="submit" disabled={isSaving}>
          {isSaving ? "Saving..." : submitLabel}
        </button>
        {onCancel && (
          <button type="button" onClick={onCancel} disabled={isSaving}>
            Cancel
          </button>
        )}
      </div>
      {error && <p className="error-text">{error}</p>}
    </form>
  );
}
