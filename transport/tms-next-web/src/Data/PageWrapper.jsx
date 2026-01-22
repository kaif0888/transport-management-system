"use client"

export default function PageWrapper ({ children }){
    return(
        <div className="flex flex-col gap-2  w-full">
        {children}
        </div>
    );
}