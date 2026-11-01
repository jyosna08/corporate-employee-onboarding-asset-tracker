import { ArrowRight } from 'lucide-react';

/** Login and HR registration form controls. */
export default function AuthForm({
  isRegistering,
  fullName,
  setFullName,
  email,
  setEmail,
  password,
  setPassword,
  confirmPassword,
  setConfirmPassword,
  error,
  notice,
  busy,
  onSubmit,
}) {
  return (
    <form onSubmit={onSubmit} className="mt-6 space-y-4">
      {isRegistering && (
        <label className="block text-[14px] font-semibold text-slate-700">
          Full name
          <input required maxLength={120} autoComplete="name" value={fullName}
            onChange={(event) => setFullName(event.target.value)} className="field"
            placeholder="Your name" />
        </label>
      )}
      <label className="block text-[14px] font-semibold text-slate-700">
        Work email
        <input required type="email" autoComplete="email" value={email}
          onChange={(event) => setEmail(event.target.value)} className="field"
          placeholder="you@company.com" />
      </label>
      <label className="block text-[14px] font-semibold text-slate-700">
        Password
        <input required type="password" minLength={8} maxLength={128}
          autoComplete={isRegistering ? 'new-password' : 'current-password'} value={password}
          onChange={(event) => setPassword(event.target.value)} className="field"
          placeholder={isRegistering ? 'Enter 8 to 128 characters' : 'Your password'} />
      </label>
      {isRegistering && (
        <label className="block text-[14px] font-semibold text-slate-700">
          Confirm password
          <input required type="password" minLength={8} maxLength={128} autoComplete="new-password"
            value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)}
            className="field" placeholder="Re-enter your password" />
        </label>
      )}
      {error && <p role="alert" className="rounded-lg bg-rose-50 p-3 text-[14px] text-rose-700">{error}</p>}
      {notice && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-[14px] text-emerald-700">{notice}</p>}
      <button disabled={busy} className="flex w-full items-center justify-center gap-2 rounded-lg bg-brand py-3 text-[14px] font-semibold text-white transition hover:bg-indigo-700 disabled:opacity-60">
        {busy ? isRegistering ? 'Creating account…' : 'Signing in…'
          : isRegistering ? 'Create HR account' : 'Sign in'}
        <ArrowRight size={14} />
      </button>
    </form>
  );
}
