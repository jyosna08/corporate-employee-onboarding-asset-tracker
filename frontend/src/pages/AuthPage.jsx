import { useState } from 'react';
import { Command, KeyRound, UserRoundPlus } from 'lucide-react';
import AuthBrandLockup from '../components/AuthBrandLockup.jsx';
import AuthForm from '../components/AuthForm.jsx';
import AuthIntroduction from '../components/AuthIntroduction.jsx';

/** Sign-in and HR self-registration screen. */
export default function AuthPage({ onLogin, onRegister }) {
  const [mode, setMode] = useState('login');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  const isRegistering = mode === 'register';

  const submit = async (event) => {
    event.preventDefault();
    setError('');
    setNotice('');
    if (isRegistering && password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setBusy(true);
    try {
      if (isRegistering) {
        await onRegister({ fullName: fullName.trim(), email: email.trim(), password });
        setMode('login');
        setPassword('');
        setConfirmPassword('');
        setNotice('HR account created. Sign in to continue.');
      } else {
        await onLogin(email.trim(), password);
      }
    } catch (submissionError) {
      setError(submissionError.message || 'Could not connect to the service.');
    } finally {
      setBusy(false);
    }
  };

  const toggleMode = () => {
    setMode(isRegistering ? 'login' : 'register');
    setError('');
    setNotice('');
  };

  return (
    <main className="grid min-h-screen bg-slate-50 lg:grid-cols-[1.05fr_.95fr]">
      <section className="relative hidden overflow-hidden bg-slate-950 p-12 text-white lg:flex lg:flex-col lg:justify-between">
        <div className="absolute -right-24 -top-24 h-96 w-96 rounded-full bg-indigo-600/25 blur-3xl" />
        <AuthBrandLockup />
        <AuthIntroduction />
        <p className="relative text-[13px] text-slate-500">
          Corporate employee onboarding &amp; asset tracker
        </p>
      </section>
      <section className="grid place-items-center p-5 sm:p-10">
        <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-7 shadow-card sm:p-9">
          <div className="mb-7 flex items-center gap-3 lg:hidden">
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-brand text-white">
              <Command size={18} />
            </span>
            <b className="font-display">PeopleOS</b>
          </div>
          <div className="grid h-11 w-11 place-items-center rounded-xl bg-indigo-50 text-indigo-600">
            {isRegistering ? <UserRoundPlus size={20} /> : <KeyRound size={20} />}
          </div>
          <h2 className="mt-5 font-display text-[26px] font-bold text-slate-900">
            {isRegistering ? 'Create your HR account' : 'Welcome back'}
          </h2>
          <p className="mt-1 text-[14px] leading-5 text-slate-500">
            {isRegistering
              ? 'Register as HR, then sign in to your workspace.'
              : 'Sign in to manage your employee workspace.'}
          </p>
          <AuthForm
            isRegistering={isRegistering}
            fullName={fullName}
            setFullName={setFullName}
            email={email}
            setEmail={setEmail}
            password={password}
            setPassword={setPassword}
            confirmPassword={confirmPassword}
            setConfirmPassword={setConfirmPassword}
            error={error}
            notice={notice}
            busy={busy}
            onSubmit={submit}
          />
          <p className="mt-6 text-center text-[14px] text-slate-500">
            {isRegistering ? 'Already registered?' : 'New to PeopleOS?'}{' '}
            <button onClick={toggleMode} className="font-semibold text-indigo-600 hover:text-indigo-800">
              {isRegistering ? 'Sign in' : 'Register as HR'}
            </button>
          </p>
          <p className="mt-5 border-t border-slate-100 pt-4 text-center text-[13px] text-slate-400">
            Registration creates an HR account. Admin and IT accounts are provisioned separately.
          </p>
        </div>
      </section>
    </main>
  );
}
